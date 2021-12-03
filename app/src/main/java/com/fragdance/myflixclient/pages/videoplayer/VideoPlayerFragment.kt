package com.fragdance.myflixclient.pages.videoplayer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.fragment.app.FragmentTransaction.*
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackGlue
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IPlayList
import com.fragdance.myflixclient.models.ISubtitle
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.pages.videoplayer.players.IVideoPlayer
import com.fragdance.myflixclient.pages.videoplayer.players.MyFlixExoPlayer
import com.fragdance.myflixclient.pages.videoplayer.players.MyFlixMediaPlayer
import com.fragdance.myflixclient.pages.videoplayer.tracks.TrackSelectionMenu
import com.fragdance.myflixclient.services.subtitleStringService
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.text.Subtitle
import com.google.android.exoplayer2.text.subrip.OpenSubtitleDecoder
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.*
import com.google.common.collect.ImmutableSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import utils.getNextVideo
import java.lang.Exception
import java.net.URI
import java.time.Duration

class VideoPlayerFragment : VideoSupportFragment() {
    private lateinit var mCurrentVideo: IVideo
    private lateinit var mPlaylist: IPlayList

    private var mVideoPlayer:IVideoPlayer? = null
    private val mViewModel: PlaybackViewModel by viewModels()
    private var mSubtitle: Subtitle? = null
    private var mTracksSelectionMenu: TrackSelectionMenu? = null

    private lateinit var mSubtitleView: SubtitleView
    var mExternalSubtitles: ArrayList<ISubtitle> = arrayListOf()
    private val uiPlaybackStateListener = object : PlaybackStateListener {
        override fun onChanged(state: VideoPlaybackState) {
            view?.keepScreenOn = state is VideoPlaybackState.Play

            when (state) {
                is VideoPlaybackState.Prepare -> startPlaybackFromWatchProgress(state.startPosition)

                is VideoPlaybackState.End -> {
                    findNavController(view!!).popBackStack()
                }
                is VideoPlaybackState.Error -> {
                }
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
        Timber.tag(Settings.TAG).d("onCreate");
        super.onCreate(savedInstanceState)
        mPlaylist = PlaybackFragmentArgs.fromBundle(requireArguments()).playlist
    }

    val onProgressUpdate: () -> Unit = {
        if (mSubtitle != null) {
            val cue = mSubtitle!!.getCues(mVideoPlayer!!.currentMs())
            mSubtitleView.setCues(cue)
        }else {
            Timber.tag(Settings.TAG).d("Got no subtitle")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag(Settings.TAG).d("onViewCreated");
        super.onViewCreated(view, savedInstanceState)
        mSubtitleView = SubtitleView(requireContext())
        (view as ViewGroup).addView(mSubtitleView)
        if (mTracksSelectionMenu == null) {
            mTracksSelectionMenu = TrackSelectionMenu()

            childFragmentManager.beginTransaction()
                .replace(R.id.tracks_selection_dock, mTracksSelectionMenu!!)
                .hide(mTracksSelectionMenu!!)
                .commit()
        }
    }

    fun onClosedCaption() {
        checkNotNull(mTracksSelectionMenu)
        checkNotNull(mVideoPlayer)
        val trackSelector = mVideoPlayer!!.getTrackSelector()
        mTracksSelectionMenu!!.setup(trackSelector, mCurrentVideo, this)
        this.hideControlsOverlay(true)
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.fragment_slide_left_enter,
            R.anim.fragment_slide_left_exit,
            R.anim.fragment_slide_left_exit,
            R.anim.fragment_slide_left_exit
        )
        ft.show(mTracksSelectionMenu!!).addToBackStack("TrackSelection2").commit()
        mTracksSelectionMenu?.view?.requestFocus()

    }

    override fun onDestroyView() {
        Timber.tag(Settings.TAG).d("onDestroyView");
        super.onDestroyView()
        mViewModel.removePlaybackStateListener(uiPlaybackStateListener)
    }

    override fun onStart() {
        Timber.tag(Settings.TAG).d("onStart");
        super.onStart()

        addVideo(mPlaylist.videos[0])
        //initializePlayer()
    }

    override fun onStop() {
        Timber.tag(Settings.TAG).d("onStop");
        super.onStop()
        destroyPlayer()
    }

    override fun onDestroy() {
        Timber.tag(Settings.TAG).d("onDestroy");
        destroyPlayer()
        super.onDestroy()

    }

    fun onPlayCompleted() {
        try {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_graph)
            navController.currentDestination?.id?.let {
                navController.popBackStack(
                    it,
                    true
                )
            }
        } catch(e: Exception) {

        }
    }

    private fun initializeExoPlayer() {
        mVideoPlayer = MyFlixExoPlayer();
        //mVideoPlayer.init(requireContext(),this )
        /*
        createMediaSession()
        mViewModel.addPlaybackStateListener(uiPlaybackStateListener)
        mSubtitleView = SubtitleView(requireContext())
        (view as ViewGroup).addView(mSubtitleView)
        if (mTracksSelectionMenu == null) {
            mTracksSelectionMenu = TrackSelectionMenu()

            childFragmentManager.beginTransaction()
                .replace(R.id.tracks_selection_dock, mTracksSelectionMenu!!)
                .hide(mTracksSelectionMenu!!)
                .commit()
        }

         */
        Timber.tag(Settings.TAG).d("initializePlayer");
        /*
        val dataSourceFactory = DefaultDataSource.Factory(
            requireContext(),
            DefaultHttpDataSource.Factory()
        )

        mTrackSelector = DefaultTrackSelector(requireContext())
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        mExoplayer =
            ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(mTrackSelector)
                .build()

        with(mExoplayer!!) {
            addListener(PlayerEventListener())
            prepare()
            prepareGlue(this)
            mMediaSessionConnector.setPlayer(this)
            mMediaSession.isActive = true
            playWhenReady = true
        }
        mExoplayer?.prepare()
        mExoplayer?.seekTo(0)
        mExoplayer?.play()
        */
    }

    private fun initializeMediaPlayer(url:String) {
        Timber.tag(Settings.TAG).d("initializeMediaPlayer");
        mVideoPlayer = MyFlixMediaPlayer()
        /*
        try {
        var glueHost = VideoSupportFragmentGlueHost(this)
        mMediaPlayerAdapter = MediaPlayerAdapter(requireContext())

        mMediaPlayerGlue = ProgressTransportControlGlue<MediaPlayerAdapter>(requireContext(),mMediaPlayerAdapter,onProgressUpdate,this)
        mMediaPlayerGlue.setHost(glueHost)
        mMediaPlayerGlue.playWhenPrepared()
        mMediaPlayerAdapter.setDataSource(Uri.parse(url))
        } catch(e:Exception) {
            Timber.tag(Settings.TAG).d(e.message)
        }

         */
        /*

        mPlaybackControlGlue = new PlaybackControlsGlue<MediaPlayerAdapter>(getActivity(), playerAdapter);
        mPlaybackControlGlue.setHost(glueHost);
        mPlaybackControlGlue.setTitle(video.getContentName());
        mPlaybackControlGlue.setSubtitle(video.getContentDesc());
        mPlaybackControlGlue.playWhenPrepared();
        try {
            playerAdapter.setDataSource(Uri.parse(video.getContentVideoUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }
         */
    }
    private fun destroyPlayer() {
        Timber.tag(Settings.TAG).d("destroyPlayer");
        if(mVideoPlayer != null) {
            mVideoPlayer!!.destroy()
            mVideoPlayer = null
        }

    }

    private fun addVideo(video: IVideo) {
        Timber.tag(Settings.TAG).d("addVideo");
        mCurrentVideo = video;
        if(video.extension == ".mp4") {
            initializeMediaPlayer(Settings.SERVER+video.url)
        } else {
            initializeExoPlayer()

        }
        mExternalSubtitles.clear()
        mExternalSubtitles.addAll(0, video.subtitles)
        mVideoPlayer!!.init(requireContext(),this )
        mVideoPlayer!!.loadVideo(video)
    }

    fun next() {
        Timber.tag(Settings.TAG).d("next");
        /*
        if (mExoplayer?.hasNextMediaItem() == true) {
            Timber.tag(Settings.TAG).d("Has next media item")
            mExoplayer?.seekToNextMediaItem()
        } else {
            if (mPlaylist.videos.size == 1) {
                Timber.tag(Settings.TAG).d("Current item %s", mCurrentVideo.title)
                mCurrentVideo = getNextVideo(mCurrentVideo)!!

                addVideo(mCurrentVideo)

                mExoplayer?.prepare()
                mExoplayer?.seekToNextMediaItem()
            }
        }

         */
    }

    fun prev() {
        /*
        mExoplayer?.seekToPreviousMediaItem()

         */
    }

    private fun startPlaybackFromWatchProgress(startPosition: Long) {
        Timber.tag(Settings.TAG).d("startPlaybakcFromWatchProgress");
        /*
        mExoplayer?.apply {
            seekTo(startPosition)
            playWhenReady = true
        }

         */
    }
/*
    private val onProgressUpdate: () -> Unit = {
        if (mSubtitle != null) {
            val cue = mSubtitle!!.getCues(mExoplayer!!.currentPosition * 1000)
            mSubtitleView.setCues(cue)
        }
    }
*/
    // Disable any internal subtitle. Used both when we're using external subtitles
    // and when we turn of subtitle
    private fun disableInternalSubtitle() {
    mVideoPlayer!!.disableInternalSubtitle()
    /*
        mExoplayer!!.trackSelectionParameters = mExoplayer!!.trackSelectionParameters
            .buildUpon()
            .setDisabledTrackTypes(ImmutableSet.of(C.TRACK_TYPE_TEXT))
            .build()
        mSubtitleView.setCues(null)

     */
    }

    // Disable all subtitles
    fun disableSubtitles() {
        mVideoPlayer!!.disableSubtitles()
        /*
        disableInternalSubtitle()
        mSubtitle = null
        mSubtitleView.setCues(null)

         */
    }

    // Convert subtitle to internal CUE
    fun prepareSrt(srt: String): Subtitle {
        val decoder = OpenSubtitleDecoder()
        return decoder.my_decode(srt.toByteArray(), srt.length, true)
    }

    fun selectExternalSubtitle(index: Int) {
        //mVideoPlayer!!.selectExternalSubtitle(index)

        disableInternalSubtitle()
        if (mExternalSubtitles[index].subtitle == null) {
            mVideoPlayer!!.pause()
            // Download external subtitle
            val requestCall = subtitleStringService.get(mExternalSubtitles[index].id)
            requestCall.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        mExternalSubtitles[index].srt = response.body() as String
                        mExternalSubtitles[index].subtitle =
                            prepareSrt(mExternalSubtitles[index].srt!!)
                        mSubtitle = mExternalSubtitles[index].subtitle
                        Timber.tag(Settings.TAG).d("Done")
                    } else {
                        Timber.tag(Settings.TAG).d("Failed")
                    }
                    mVideoPlayer!!.play()
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    mVideoPlayer!!.play()
                }
            })
        } else {
            Timber.tag(Settings.TAG).d("Testing")
            mSubtitle = mExternalSubtitles[index].subtitle
            mVideoPlayer!!.play()
        }

    }

    fun downloadSubtitle(subtitle: ISubtitle) {

        val url: String = subtitle.url
        val requestCall = subtitleStringService.downloadSubtitle(url)

        requestCall.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {

                    val sub = response.body() as String

                    val decoder = OpenSubtitleDecoder()
                    mSubtitle = decoder.my_decode(sub.toByteArray(), sub.length, true)
                    mExternalSubtitles.add(
                        ISubtitle(
                            -1,
                            subtitle.url,
                            subtitle.language,
                            subtitle.filename,
                            sub,
                            mSubtitle
                        )
                    )
                    Timber.tag(Settings.TAG).d("Got my subtitle")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.tag(Settings.TAG).d(t)
            }
        })


    }

    inner class PlayerEventListener : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            /*
            Timber.tag(Settings.TAG).d("onMediaItemTransition");
            if (mediaItem?.localConfiguration != null) {
                val video: IVideo = mediaItem.localConfiguration!!.tag as IVideo
                getNextVideo(video)
                Timber.tag(Settings.TAG).d(video.title)
                mExoPlayerGlue.title = video.title
                mCurrentVideo = video
            }

             */
        }

        override fun onCues(cues: MutableList<Cue>) {
            super.onCues(cues)
            mSubtitleView.setCues(cues)

        }

    }

    companion object {
        const val MEDIA_SESSION_TAG = "myflix_token"
        val PLAYER_UPDATE_INTERVAL_MILLIS = Duration.ofMillis(50).toMillis()
    }
}

