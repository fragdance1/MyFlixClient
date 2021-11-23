package com.fragdance.myflixclient.pages.videoplayer

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.fragment.app.FragmentTransaction.*
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackGlue
import androidx.navigation.Navigation.findNavController
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.pages.videoplayer.tracks.TrackSelectionMenu
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.MimeTypes
import java.time.Duration

class VideoPlayerFragment : VideoSupportFragment() {
    // Change this to video in the future
    private lateinit var video: IMovie

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var trackSelector: DefaultTrackSelector
    private var exoplayer: ExoPlayer? = null
    private val viewModel: PlaybackViewModel by viewModels()
    private lateinit var subtitleView: SubtitleView
    private var tracksSelection: TrackSelectionMenu? = null

    private val uiPlaybackStateListener = object : PlaybackStateListener {
        override fun onChanged(state: VideoPlaybackState) {
            view?.keepScreenOn = state is VideoPlaybackState.Play

            when (state) {
                is VideoPlaybackState.Prepare -> startPlaybackFromWatchProgress(state.startPosition)

                is VideoPlaybackState.End -> {
                    findNavController(view!!).popBackStack()
                }
                is VideoPlaybackState.Error ->
                    findNavController(view!!).navigate(
                        PlaybackFragmentDirections
                            .actionPlaybackFragmentToPlaybackErrorFragment(
                                state.video,
                                state.exception
                            )
                    )
                else -> {
                    // Do nothing.
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        video = PlaybackFragmentArgs.fromBundle(requireArguments()).video
        createMediaSession()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.addPlaybackStateListener(uiPlaybackStateListener)
        subtitleView = SubtitleView(requireContext())
        (view as ViewGroup).addView(subtitleView)
        if (tracksSelection == null) {
            tracksSelection = TrackSelectionMenu();

            childFragmentManager.beginTransaction()
                .replace(R.id.tracks_selection_dock, tracksSelection!!)
                .hide(tracksSelection!!)
                .commit()
        }
    }

    fun onClosedCaption() {
        checkNotNull(tracksSelection)

        tracksSelection!!.setTracks(trackSelector)
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.fragment_slide_left_enter,
            R.anim.fragment_slide_left_exit,
            R.anim.fragment_slide_left_exit,
            R.anim.fragment_slide_left_exit
        )
        ft.show(tracksSelection!!).addToBackStack("TrackSelection2").commit()
        tracksSelection?.view?.requestFocus()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removePlaybackStateListener(uiPlaybackStateListener)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        destroyPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }

    private fun initializePlayer() {
        val dataSourceFactory = DefaultDataSource.Factory(
            requireContext(),
            DefaultHttpDataSource.Factory()
        )

        trackSelector = DefaultTrackSelector(requireContext())
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        val url: String = Settings.SERVER + "/api/video/local?id=" + video.video_files[0].id

        // Load any external subtitles
        val subtitles: ArrayList<MediaItem.SubtitleConfiguration> = arrayListOf()

        for (subtitle in video.video_files[0].subtitles) {
            val sub: String = Settings.SERVER + "/api/subtitle/get?id=" + subtitle.id
            val language = subtitle.language
            val selectionFlags = C.SELECTION_FLAG_FORCED

            val subtitleItem = MediaItem.SubtitleConfiguration.Builder(Uri.parse(sub))
                .setMimeType(MimeTypes.APPLICATION_SUBRIP) // The correct MIME type (required).
                .setLanguage(language) // The subtitle language (optional).
                .setSelectionFlags(selectionFlags) // Selection flags for the track (optional).
                .setRoleFlags(C.ROLE_FLAG_CAPTION)
                .build()
            subtitles.add(subtitleItem)
        }

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setSubtitleConfigurations(subtitles)
            .build()

        exoplayer =
            ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector)
                .build()

        with(exoplayer!!) {
            setMediaItem(mediaItem)
            addListener(PlayerEventListener())
            prepare()
            prepareGlue(this)
            mediaSessionConnector.setPlayer(this)
            mediaSession.isActive = true
            playWhenReady = true
        }

        viewModel.onStateChange(VideoPlaybackState.Load(video))
    }

    private fun destroyPlayer() {
        mediaSession.isActive = false
        mediaSessionConnector.setPlayer(null)
        exoplayer?.let {
            // Pause the player to notify listeners before it is released.
            it.pause()
            it.release()
            exoplayer = null
        }
    }

    private fun prepareGlue(localExoplayer: ExoPlayer) {
        val glue = ProgressTransportControlGlue(
            requireContext(),
            LeanbackPlayerAdapter(
                requireContext(),
                localExoplayer,
                PLAYER_UPDATE_INTERVAL_MILLIS.toInt()
            ),

            onProgressUpdate,
            this
        ).apply {
            host = VideoSupportFragmentGlueHost(this@VideoPlayerFragment)
            title = video.title

            isSeekEnabled = true
            addPlayerCallback(object : PlaybackGlue.PlayerCallback() {
                override fun onPlayCompleted(glue: PlaybackGlue?) {
                    super.onPlayCompleted(glue)
                    val navController = findNavController(requireActivity(), R.id.nav_graph)
                    navController.currentDestination?.id?.let {
                        navController.popBackStack(
                            it,
                            true
                        )
                    }
                }
            })
        }
        glue.isControlsOverlayAutoHideEnabled = true
        glue.host.isControlsOverlayAutoHideEnabled = true


    }

    private fun createMediaSession() {
        mediaSession = MediaSessionCompat(requireContext(), MEDIA_SESSION_TAG)

        // Connect media session to player (exoplayer)
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            // Handle playlist navigation
            //setQueueNavigator(SingleViewQueueNavigator)
        }
    }

    private fun startPlaybackFromWatchProgress(startPosition: Long) {
        exoplayer?.apply {
            seekTo(startPosition)
            playWhenReady = true
        }
    }

    private val onProgressUpdate: () -> Unit = {
        //Timber.tag(Settings.TAG).d("onProgressUpdate")
    }

    inner class PlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_READY) {

            }
        }

        override fun onCues(cues: MutableList<Cue>) {
            super.onCues(cues)
            subtitleView.setCues(cues)
        }
    }

    companion object {
        private const val MEDIA_SESSION_TAG = "myflix_token"
        private val PLAYER_UPDATE_INTERVAL_MILLIS = Duration.ofMillis(50).toMillis()
    }
}

