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
import com.fragdance.myflixclient.models.IOpenSubtitle
import com.fragdance.myflixclient.models.IPlayList
import com.fragdance.myflixclient.models.ISubtitle
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.pages.videoplayer.tracks.TrackSelectionMenu
import com.fragdance.myflixclient.services.subtitleService
import com.fragdance.myflixclient.services.subtitleStringService
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.text.Subtitle
import com.google.android.exoplayer2.text.SubtitleDecoderFactory
import com.google.android.exoplayer2.text.SubtitleExtractor
import com.google.android.exoplayer2.text.subrip.OpenSubtitleDecoder
import com.google.android.exoplayer2.text.subrip.SubripDecoder

import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.MimeTypes
import com.google.common.collect.ImmutableSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import utils.getNextVideo
import java.time.Duration

class VideoPlayerFragment : VideoSupportFragment() {
    // Change this to video in the future
    private lateinit var currentItem:IVideo;
    private lateinit var playlist: IPlayList
    private lateinit var playerGlue:ProgressTransportControlGlue<LeanbackPlayerAdapter>
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var trackSelector: DefaultTrackSelector
    var exoplayer: ExoPlayer? = null
    private val viewModel: PlaybackViewModel by viewModels()
    private lateinit var subtitleView: SubtitleView
    private var mSubtitle: Subtitle? = null
    private var tracksSelection: TrackSelectionMenu? = null

    public var mExternalSubtitles:ArrayList<ISubtitle> = arrayListOf()

    private val uiPlaybackStateListener = object : PlaybackStateListener {
        override fun onChanged(state: VideoPlaybackState) {
            view?.keepScreenOn = state is VideoPlaybackState.Play

            when (state) {
                is VideoPlaybackState.Prepare -> startPlaybackFromWatchProgress(state.startPosition)

                is VideoPlaybackState.End -> {
                    findNavController(view!!).popBackStack()
                }
                is VideoPlaybackState.Error ->{}
                   /* findNavController(view!!).navigate(
                        PlaybackFragmentDirections
                            .actionPlaybackFragmentToPlaybackErrorFragment(
                                state.video,
                                state.exception
                            )
                    )*/
                else -> {
                    // Do nothing.
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlist = PlaybackFragmentArgs.fromBundle(requireArguments()).playlist
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

        tracksSelection!!.setup(trackSelector,currentItem,this)
        this.hideControlsOverlay(true)
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

        exoplayer =
            ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector)
                .build()

        with(exoplayer!!) {
            addListener(PlayerEventListener())
            prepare()
            prepareGlue(this)
            mediaSessionConnector.setPlayer(this)
            mediaSession.isActive = true
            playWhenReady = true
        }
        for(video in playlist.videos) {
            addVideo(video);
        }
        exoplayer?.prepare()
        exoplayer?.seekTo(0)
        exoplayer?.play()
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

    private fun addVideo(video: IVideo) {
        val url: String = Settings.SERVER + video.url
        // Load any external subtitlesT
        //val subtitles: ArrayList<MediaItem.SubtitleConfiguration> = arrayListOf()
        mExternalSubtitles.clear();
        mExternalSubtitles.addAll(0,video.subtitles);


/*
        for (subtitle in video.subtitles) {
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
*/
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setTag(video)
            .build()
        with(exoplayer!!) {
            addMediaItem(mediaItem)
        }

    }


    fun next() {
        if(exoplayer?.hasNextMediaItem() == true) {
            Timber.tag(Settings.TAG).d("Has next media item");
            exoplayer?.seekToNextMediaItem()
        } else {
            if(playlist.videos.size == 1) {
                Timber.tag(Settings.TAG).d("Current item " + currentItem.title);
                currentItem = getNextVideo(currentItem)!!;

                addVideo(currentItem)

                exoplayer?.prepare();
                exoplayer?.seekToNextMediaItem()
            }
        }
    }

    fun prev() {
        exoplayer?.seekToPreviousMediaItem()
    }

    private fun prepareGlue(localExoplayer: ExoPlayer) {
        playerGlue = ProgressTransportControlGlue(
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
            //title = video.title

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
        (playerGlue as ProgressTransportControlGlue<LeanbackPlayerAdapter>).isControlsOverlayAutoHideEnabled = true
        playerGlue.host.isControlsOverlayAutoHideEnabled = true


    }

    private fun createMediaSession() {
        mediaSession = MediaSessionCompat(requireContext(), MEDIA_SESSION_TAG)

        // Connect media session to player (exoplayer)
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            // Handle playlist navigation
            //setQueueNavigator(PlaylistQueueNavigation(mediaSession,video))
        }
    }

    private fun startPlaybackFromWatchProgress(startPosition: Long) {
        exoplayer?.apply {
            seekTo(startPosition)
            playWhenReady = true
        }
    }

    private val onProgressUpdate: () -> Unit = {

        if(mSubtitle != null) {
            var cue = mSubtitle!!.getCues(exoplayer!!.currentPosition * 1000);
            subtitleView.setCues(cue)
        }
    }

    // Disable any internal subtitle. Used both when we're using external subtitles
    // and when we turn of subtitle
    fun disableInternalSubtitle() {
        exoplayer!!.setTrackSelectionParameters(
            exoplayer!!.getTrackSelectionParameters()
                .buildUpon()
                .setDisabledTrackTypes(ImmutableSet.of(C.TRACK_TYPE_TEXT))
                .build());
        subtitleView.setCues(null)
    }

    // Disable all subtitles
    fun disableSubtitles() {
        disableInternalSubtitle()
        mSubtitle = null;
        subtitleView.setCues(null)
    }

    fun prepareSrt(srt:String):Subtitle {
        val decoder = OpenSubtitleDecoder()
        return decoder.my_decode(srt.toByteArray(),srt.length,true)
    }
    fun selectExternalSubtitle(index:Int) {
        disableInternalSubtitle()
        if(mExternalSubtitles[index].subtitle == null) {
            exoplayer!!.pause();
            // Download external subtitle
            val requestCall = subtitleStringService.get(mExternalSubtitles[index].id)
            requestCall.enqueue(object: Callback<String>{
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if(response.isSuccessful) {
                        mExternalSubtitles[index].srt = response.body() as String
                        mExternalSubtitles[index].subtitle = prepareSrt(mExternalSubtitles[index].srt!!)
                        mSubtitle = mExternalSubtitles[index].subtitle
                        Timber.tag(Settings.TAG).d("Done")
                    } else {
                        Timber.tag(Settings.TAG).d("Failed")
                    }
                    exoplayer!!.play()
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Timber.tag(Settings.TAG).d("Failed "+t.message)
                    exoplayer!!.play()
                }
            })
        } else {
            Timber.tag(Settings.TAG).d("Testing")
            mSubtitle = mExternalSubtitles[index].subtitle
            exoplayer!!.play()
        }

    }
    fun downloadSubtitle(subtitle:ISubtitle) {
        val url: String =  subtitle.url
        val requestCall = subtitleStringService.downloadSubtitle(url)

        requestCall.enqueue(object: Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if(response.isSuccessful) {

                    var sub = response.body() as String

                    val decoder = OpenSubtitleDecoder()
                    mSubtitle = decoder.my_decode(sub.toByteArray(),sub.length,true)
                    var subtitle = ISubtitle(-1,subtitle.url,subtitle.language,subtitle.filename,sub,mSubtitle)
                    mExternalSubtitles.add(subtitle)
                    Timber.tag(Settings.TAG).d("Got my subtitle")
                } else {

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.tag(Settings.TAG).d(t.message)
            }
        })

        /*
            val language = subtitle.langcode
            val selectionFlags = C.SELECTION_FLAG_FORCED

            val subtitleItem = MediaItem.SubtitleConfiguration.Builder(Uri.parse(sub))
                .setMimeType(MimeTypes.APPLICATION_SUBRIP) // The correct MIME type (required).
                .setLanguage(language) // The subtitle language (optional).
                .setSelectionFlags(selectionFlags) // Selection flags for the track (optional).
                .setRoleFlags(C.ROLE_FLAG_CAPTION)
                .build()


        var mediaItem = MediaItem.Builder().setSubtitleConfigurations(listOf(subtitleItem)).build()

        exoplayer!!.addMediaSource(DefaultMediaSourceFactory(context!!).createMediaSource(mediaItem))

         */
    }
    inner class PlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_READY) {

            }
        }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if(mediaItem != null && mediaItem.localConfiguration != null) {
                var video:IVideo = mediaItem.localConfiguration!!.tag as IVideo
                getNextVideo(video);
                Timber.tag(Settings.TAG).d(video.title)
                playerGlue.title = video.title
                currentItem = video;
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

