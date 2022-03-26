package com.fragdance.myflixclient.pages.videoplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.*
import androidx.fragment.app.FragmentTransaction.*
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackGlue
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.side_menu.SideMenu
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

import java.lang.Exception
import java.net.URI
import java.time.Duration

class VideoPlayerFragment : VideoSupportFragment() {
    // The video currently playing
    private lateinit var mCurrentVideo: IVideo
    // The list of videos to play. At least one
    private lateinit var mPlaylist: IPlayList

    // The current video player (MediaPlayer/Exoplayer denpending on format)
    private var mVideoPlayer:IVideoPlayer? = null
    private val mViewModel: PlaybackViewModel by viewModels()

    // The currently active external subtitle
    private var mSubtitle: Subtitle? = null
    private var mTracksSelectionMenu: TrackSelectionMenu? = null

    // View from exoplayer for displaying subtitles
    private lateinit var mSubtitleView: SubtitleView
    // A list of external subtitles for this video
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

    // Update listener for syncing external subtitles
    val onProgressUpdate: () -> Unit = {
        if (mSubtitle != null && mVideoPlayer != null) {
            val cue = mSubtitle!!.getCues(mVideoPlayer!!.currentMs())
            mSubtitleView.setCues(cue)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlaylist = PlaybackFragmentArgs.fromBundle(requireArguments()).playlist
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        Timber.tag(Settings.TAG).d("VideoPlayerFragment.onCreateView")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<View>(R.id.side_menu)?.visibility = INVISIBLE;
        mSubtitleView = SubtitleView(requireContext())
        (view as ViewGroup).addView(mSubtitleView)

        // Create the subtitle/audio selection if needed
        if (mTracksSelectionMenu == null) {
            mTracksSelectionMenu = TrackSelectionMenu()

            childFragmentManager.beginTransaction()
                .replace(R.id.tracks_selection_dock, mTracksSelectionMenu!!)
                .hide(mTracksSelectionMenu!!)
                .commit()
        }

    }

    fun onClosedCaption() {
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
        ft.show(mTracksSelectionMenu!!).addToBackStack("TrackSelectionMenu").commit()
        mTracksSelectionMenu?.view?.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //activity?.findViewById<View>(R.id.side_menu)?.visibility = VISIBLE;

        mViewModel.removePlaybackStateListener(uiPlaybackStateListener)
        /*
        val navController = findNavController(this.view!!)
        navController.currentDestination?.id?.let {
            navController.popBackStack(it, true)
        }

         */
    }

    override fun onStart() {
        super.onStart()
        addVideo(mPlaylist.videos[0])
    }

    override fun onStop() {
        super.onStop()
        destroyPlayer()
    }

    override fun onDestroy() {
        destroyPlayer()
        super.onDestroy()
    }

    fun onPlayCompleted() {
        //Timber.tag(Settings.TAG).d("onPlayCompleted")
        try {
            val navController = findNavController(this.view!!)
            navController.currentDestination?.id?.let {
                navController.popBackStack(it, true)
            }
        } catch(e: Exception) {
            Timber.tag(Settings.TAG).d("Error "+e.message)
        }
    }

    private fun initializeExoPlayer() {
        mVideoPlayer = MyFlixExoPlayer();
    }

    private fun initializeMediaPlayer() {
        mVideoPlayer = MyFlixMediaPlayer()
    }

    private fun destroyPlayer() {
        if(mVideoPlayer != null) {
            mVideoPlayer!!.destroy()
            mVideoPlayer = null
        }
    }

    private fun addVideo(video: IVideo) {
        mCurrentVideo = video;
        if(video.extension == ".avi") {
            initializeMediaPlayer()
        } else {
            initializeExoPlayer()

        }
        mExternalSubtitles.clear()
        mExternalSubtitles.addAll(0, video.subtitles)
        mVideoPlayer!!.init(requireContext(),this )
        mVideoPlayer!!.loadVideo(video)

    }

    fun next() {
        Timber.tag(Settings.TAG).d("next")
    }

    fun prev() {
        Timber.tag(Settings.TAG).d("prev")
    }

    private fun startPlaybackFromWatchProgress(startPosition: Long) {
        //Timber.tag(Settings.TAG).d("startPlaybakcFromWatchProgress");
    }

    // Disable any internal subtitle. Used both when we're using external subtitles
    // and when we turn of subtitle
    private fun disableInternalSubtitle() {
        mVideoPlayer!!.disableInternalSubtitle()
    }

    // Disable all subtitles
    fun disableSubtitles() {
        mVideoPlayer!!.disableInternalSubtitle()
        mSubtitle = null;
        mSubtitleView.setCues(null)
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
                        //Timber.tag(Settings.TAG).d("Done")
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
            //Timber.tag(Settings.TAG).d("Testing")
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
                    //Timber.tag(Settings.TAG).d("Got my subtitle")
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

