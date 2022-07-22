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
import android.view.WindowManager
import android.widget.TextView
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
import com.fragdance.myflixclient.models.IStatus
import com.fragdance.myflixclient.models.ISubtitle
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.pages.videoplayer.players.IVideoPlayer
import com.fragdance.myflixclient.pages.videoplayer.players.MyFlixExoPlayer
import com.fragdance.myflixclient.pages.videoplayer.players.MyFlixMediaPlayer
import com.fragdance.myflixclient.pages.videoplayer.tracks.TrackSelectionMenu
import com.fragdance.myflixclient.services.FayeService
import com.fragdance.myflixclient.services.subtitleStringService
import com.fragdance.myflixclient.services.torrentService
import com.fragdance.myflixclient.services.trakttvService
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
import org.cometd.bayeux.Message
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

import java.lang.Exception
import java.net.URI
import java.time.Duration
import java.time.LocalDateTime

fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
    when (val value = this[it])
    {
        is JSONArray ->
        {
            val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
            JSONObject(map).toMap().values.toList()
        }
        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        else            -> value
    }
}

class VideoPlayerFragment : VideoSupportFragment() {
    // The video currently playing
    private lateinit var mCurrentVideo: IVideo
    // The list of videos to play. At least one
    private lateinit var mPlaylist: IPlayList
    // The current video player (MediaPlayer/Exoplayer denpending on format)
    private var mVideoPlayer:IVideoPlayer? = null
    //private val mViewModel: PlaybackViewModel by viewModels()

    // The currently active external subtitle
    private var mSubtitle: Subtitle? = null
    private var mTracksSelectionMenu: TrackSelectionMenu? = null

    // View from exoplayer for displaying subtitles
    private lateinit var mSubtitleView: SubtitleView
    // A list of external subtitles for this video
    var mExternalSubtitles: ArrayList<ISubtitle> = arrayListOf()

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
        Timber.tag(Settings.TAG).d(mPlaylist.videos.toString())
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
        if(mCurrentVideo?.hash != null) {
            FayeService.unsubscribe("/torrent/"+mCurrentVideo.hash)
        }
        activity?.findViewById<View>(R.id.side_menu)?.visibility = VISIBLE;
        activity?.findViewById<View>(R.id.side_menu)?.clearFocus()
    }

    override fun onStart() {
        super.onStart()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        addVideo(mPlaylist.videos[mPlaylist.position.toInt()])
    }

    override fun onStop() {
        super.onStop()
        destroyPlayer()
    }

    override fun onDestroy() {
        destroyPlayer()
        activity?.findViewById<View>(R.id.loading_progress)?.visibility = INVISIBLE;
        super.onDestroy()
    }

    fun onPlayCompleted() {
        destroyPlayer()
        val date = LocalDateTime.now().toString()
        val access_token = "73f7cc828bb000a3fcf37c87e43b37d2128baddf248c5accdc4dfb6014346593"
        val tmdbId = mCurrentVideo.tmdbId.toString()

        var watchedCall = if(mCurrentVideo.type == "movie") trakttvService.setMovieWatched(access_token,tmdbId,date) else trakttvService.setEpisodeWatched(access_token,tmdbId,date)
        watchedCall.enqueue(object:Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Timber.tag(Settings.TAG).d("TraktTV started")
                //TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                //TODO("Not yet implemented")
                Timber.tag(Settings.TAG).d("TraktTV failed")
            }
        })

        mPlaylist.position++;

        if(mPlaylist.videos.size > mPlaylist.position) {
            addVideo(mPlaylist.videos[mPlaylist.position.toInt()])
        } else {
            try {
                val navController = findNavController(this.view!!)
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                navController.currentDestination?.id?.let {
                    navController.popBackStack(it, true)
                }
            } catch (e: Exception) {
                Timber.tag(Settings.TAG).d("Error " + e.message)
            }
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
            val progress = mVideoPlayer!!.getProgress();
            val access_token = "73f7cc828bb000a3fcf37c87e43b37d2128baddf248c5accdc4dfb6014346593"
            val tmdbId = mCurrentVideo.tmdbId.toString()
            val imdbId = mCurrentVideo.imdbId.toString()
            Timber.tag(Settings.TAG).d("Progress is "+progress);
            val startCall = if(mCurrentVideo.type == "movie")
                trakttvService.stopMovie(access_token,imdbId,progress)
            else
                trakttvService.stopEpisode(access_token,tmdbId,progress)
            startCall.enqueue(object:Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    Timber.tag(Settings.TAG).d("TraktTV stopped")
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Timber.tag(Settings.TAG).d("TraktTV failed")
                }
            })
            mVideoPlayer!!.destroy()
            mVideoPlayer = null

        }
    }

    private fun startVideo(video:IVideo) {
        mCurrentVideo = video;
        if(video.extension == ".avi") {
            initializeMediaPlayer()
        } else {
            initializeExoPlayer()
        }
        mExternalSubtitles.clear()
        if(video?.subtitles != null) {
            mExternalSubtitles.addAll(0, video.subtitles)

        }
        mVideoPlayer!!.init(requireContext(),this )

        mVideoPlayer!!.loadVideo(video)
        if(video!!.subtitles.count() > 0) {
            selectExternalSubtitle(0)
        } else {
            disableSubtitles();
        }
    }

    private fun addTorrent(video:IVideo) {
        activity?.findViewById<View>(R.id.loading_progress)?.visibility = VISIBLE;
        activity?.findViewById<TextView>(R.id.loadingStatus)?.text = "Preparing torrent"
        val addMovieTorrentCall =
            torrentService.addMovieTorrent(video.url!!, video.id,
                video.hash!!
            )
        addMovieTorrentCall.enqueue(object : Callback<IStatus> {
            override fun onResponse(
                call: Call<IStatus>,
                response: Response<IStatus>
            ) {
                Timber.tag(Settings.TAG).d("Torrent function returned")
                val url =
                    "/api/video/torrent/" + video.hash
            }

            override fun onFailure(call: Call<IStatus>, t: Throwable) {
                Timber.tag(Settings.TAG).d("Adding torrent exception " + t)
            }

        })
    }
    private fun addVideo(video: IVideo) {
        mCurrentVideo = video;

        // See if we have a torrent (ie hash != null)
        if(video.hash != null) {
            var ready = false
            // Fire up faye

            FayeService.subscribe("/torrent/"+video.hash) { message ->
                run {
                    val jsonObj = JSONObject(message)
                    val obj = jsonObj.toMap()
                        //val obj = message?.dataAsMap;
                        val event = obj["event"]
                        if(event == "TORRENT_ADDED") {
                            Timber.tag(Settings.TAG).d("TORRENT_ADDED")
                            activity?.runOnUiThread {
                                activity?.findViewById<TextView>(R.id.loadingStatus)?.text =
                                    obj["msg"].toString();
                            }
                        }
                        if(event == "TORRENT_BUFFERING") {
                            Timber.tag(Settings.TAG).d(obj["msg"].toString())
                            activity?.runOnUiThread {
                                activity?.findViewById<TextView>(R.id.loadingStatus)?.text =
                                    obj["msg"].toString();
                            }
                        }
                        if(event == "TORRENT_READY") {
                            ready = true
                            try {
                                activity?.runOnUiThread {

                                        activity?.findViewById<View>(R.id.loading_progress)?.visibility = View.INVISIBLE;

                                    var url = "/api/video/torrent/" + video.hash
                                    val video = IVideo(
                                        video.id,
                                        "mkv",
                                        video.title,
                                        video.poster,
                                        video.overview,
                                        url,
                                        video.hash,
                                        emptyList(),

                                        video.type,
                                        video.tmdbId,
                                        video.imdbId
                                    )

                                    startVideo(video)
                                }
                            } catch (e:Exception){
                                Timber.tag(Settings.TAG).d("Exception in runon")
                            }

                            // Start playing

                        }

                        //FayeService.handshake()

                }
            }
            // Add torrent

            Timber.tag(Settings.TAG).d("Got a torrent video")
            addTorrent(video)

        } else {
            startVideo(video)
        }

    }

    fun next() {
        mPlaylist.position++;
        if(mPlaylist.videos.size > mPlaylist.position) {
            addVideo(mPlaylist.videos[mPlaylist.position.toInt()])
        }
    }

    fun prev() {
        Timber.tag(Settings.TAG).d("prev")
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
        Timber.tag(Settings.TAG).d("selectExternalSubtitle")
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

    fun downloadSubtitle(subtitle: ISubtitle,videoId:Long) {

        val url: String = subtitle.url
        val requestCall = subtitleStringService.downloadSubtitle(url,videoId)
        Timber.tag(Settings.TAG).d("Downloading subtitle")
        requestCall.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    Timber.tag(Settings.TAG).d("Subtitle downloaded");
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
        override fun onIsPlayingChanged(isPlaying: Boolean) {

            super.onIsPlayingChanged(isPlaying)
            if(isPlaying) {
                val access_token = "73f7cc828bb000a3fcf37c87e43b37d2128baddf248c5accdc4dfb6014346593"
                val tmdbId = mCurrentVideo.tmdbId.toString()
                var imdbId = mCurrentVideo.imdbId.toString()
                val startCall = if(mCurrentVideo.type == "movie")
                    trakttvService.startMovie(access_token,imdbId,"0")
                else
                    trakttvService.startEpisode(access_token,tmdbId,"0")
                startCall.enqueue(object:Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        Timber.tag(Settings.TAG).d("TraktTV started")
                    }
                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Timber.tag(Settings.TAG).d("TraktTV failed")
                    }
                })
            }
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

