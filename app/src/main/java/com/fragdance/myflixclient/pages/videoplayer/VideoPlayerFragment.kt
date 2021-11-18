package com.fragdance.myflixclient.pages.videoplayer

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.navigation.Navigation.findNavController
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IMovie
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.*
import timber.log.Timber
import java.time.Duration
import com.google.android.exoplayer2.util.MimeTypes
import com.google.common.collect.ImmutableList
import com.google.android.exoplayer2.text.Cue

import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.*

class VideoPlayerFragment : VideoSupportFragment() {
    private lateinit var video: IMovie
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private var exoplayer: ExoPlayer? = null
    private val viewModel: PlaybackViewModel by viewModels()
    private lateinit var subtitleView: SubtitleView

    private val uiPlaybackStateListener = object : PlaybackStateListener {
        override fun onChanged(state: VideoPlaybackState) {
            view?.keepScreenOn = state is VideoPlaybackState.Play
            Timber.tag(Settings.TAG).d("onChanged " + exoplayer?.currentCues?.size);
            // Switch/case a'la kotlin
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
        subtitleView = SubtitleView(requireContext());
        (view as ViewGroup).addView(subtitleView);
    }

    override fun onDestroyView() {
        Timber.tag(Settings.TAG).d("VideoPlayerFragment.onDestroyView")
        super.onDestroyView()
        //viewModel.removePlaybackStateListener(uiPlaybackStateListener)
    }

    override fun onStart() {
        Timber.tag(Settings.TAG).d("VideoPlayerFragment.onStart")
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        Timber.tag(Settings.TAG).d("VideoPlayerFragment.onStop")
        super.onStop()
        destroyPlayer()
    }

    override fun onDestroy() {
        Timber.tag(Settings.TAG).d("VideoPlayerFragment.onDestroy")
        super.onDestroy()
        mediaSession.release()
    }

    private fun initializePlayer() {
        Timber.tag(Settings.TAG).d("VideoPlayerFragment.initializePlayer")
        val dataSourceFactory = DefaultDataSource.Factory(
            requireContext(),
            DefaultHttpDataSource.Factory()
        )

        val trackSelector = DefaultTrackSelector(requireContext())
        //val renderersFactory = DefaultRenderersFactory(requireActivity().applicationContext)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        val url: String = Settings.SERVER + "/api/video/local?id=" + video.video_files[0].id

        // Load any external subtitles
        var subtitles: ArrayList<MediaItem.SubtitleConfiguration> = arrayListOf();

        for (subtitle in video.video_files[0].subtitles) {
            val sub: String = Settings.SERVER + "/api/subtitle/get?id=" + subtitle.id;
            val language = subtitle.language;
            val selectionFlags = C.SELECTION_FLAG_FORCED;

            val subtitle = MediaItem.SubtitleConfiguration.Builder(Uri.parse(sub))
                .setMimeType(MimeTypes.APPLICATION_SUBRIP) // The correct MIME type (required).
                .setLanguage(language) // The subtitle language (optional).
                .setSelectionFlags(selectionFlags) // Selection flags for the track (optional).
                .setRoleFlags(C.ROLE_FLAG_CAPTION)
                .build()
            subtitles.add(subtitle);
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
        exoplayer!!.setMediaItem(mediaItem);
        exoplayer!!.addListener(PlayerEventListener())
        exoplayer!!.addAnalyticsListener(EventLogger(trackSelector));

        exoplayer?.prepare()
        prepareGlue(exoplayer!!)
        mediaSessionConnector.setPlayer(exoplayer!!)
        mediaSession.isActive = true
        exoplayer?.playWhenReady = true

        viewModel.onStateChange(VideoPlaybackState.Load(video))
        Timber.tag(Settings.TAG).d("Done preparing");
    }

    private fun destroyPlayer() {
        Timber.tag(Settings.TAG).d("VideoPlayerFragment.destroyPlayer")
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
        Timber.tag(Settings.TAG).d("VideoPlayerFragment.prepareGlue")
        ProgressTransportControlGlue(
            requireContext(),
            LeanbackPlayerAdapter(
                requireContext(),
                localExoplayer,
                PLAYER_UPDATE_INTERVAL_MILLIS.toInt()
            ),
            onProgressUpdate
        ).apply {
            host = VideoSupportFragmentGlueHost(this@VideoPlayerFragment)
            title = video.title
            // Enable seek manually since PlaybackTransportControlGlue.getSeekProvider() is null,
            // so that PlayerAdapter.seekTo(long) will be called during user seeking.
            // TODO(gargsahil@): Add a PlaybackSeekDataProvider to support video scrubbing.
            isSeekEnabled = true
        }
    }

    private fun createMediaSession() {
        Timber.tag(Settings.TAG).d("VideoPlayerFragment.createMediaSession");
        mediaSession = MediaSessionCompat(requireContext(), MEDIA_SESSION_TAG);

        // Connect media session to player (exoplayer)
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            // Handle playlist navigation
            //setQueueNavigator(SingleViewQueueNavigator)

        }

        exoplayer?.apply {
            playWhenReady = true;
        }
    }

    private fun startPlaybackFromWatchProgress(startPosition: Long) {
        Timber.v("Starting playback from $startPosition")
        exoplayer?.apply {
            seekTo(startPosition)
            playWhenReady = true
        }
    }

    private val onProgressUpdate: () -> Unit = {
        Timber.tag(Settings.TAG).d("onProgressUpdate");
    }

    inner class PlayerEventListener : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Timber.tag(Settings.TAG).d("Error occured " + error.errorCodeName);
        }

        override fun onCues(cues: MutableList<Cue>) {
            super.onCues(cues)
            Timber.tag(Settings.TAG).d("onCues " + cues.size)
            subtitleView.setCues(cues);
        }
    }

    companion object {
        private const val MEDIA_SESSION_TAG = "myflix_token"
        private const val ARGUMENT_VIDEO = "movie"
        private val PLAYER_UPDATE_INTERVAL_MILLIS = Duration.ofMillis(50).toMillis()
        fun newInstance(videoItem: IMovie) = VideoPlayerFragment().apply {
            arguments = bundleOf(
                ARGUMENT_VIDEO to videoItem
            )
        }
    }
}