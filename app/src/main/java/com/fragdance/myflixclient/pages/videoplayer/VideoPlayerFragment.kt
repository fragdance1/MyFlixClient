package com.fragdance.myflixclient.pages.videoplayer

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.leanback.app.VideoSupportFragment
import androidx.navigation.Navigation.findNavController
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.*
import com.fragdance.myflixclient.pages.videoplayer.players.IVideoPlayer
import com.fragdance.myflixclient.pages.videoplayer.players.MyFlixExoPlayer
import com.fragdance.myflixclient.pages.videoplayer.players.MyFlixMediaPlayer
import com.fragdance.myflixclient.pages.videoplayer.tracks.TrackSelectionMenu
import com.fragdance.myflixclient.services.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.text.Subtitle
import com.google.android.exoplayer2.text.subrip.OpenSubtitleDecoder
import com.google.android.exoplayer2.ui.SubtitleView
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception
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
    // Progress in the current video (for resuming)
    private var mProgress: Float = 0.0f
    // The current video player (MediaPlayer/Exoplayer denpending on format)
    private var mVideoPlayer:IVideoPlayer? = null

    // The currently active external subtitle
    private var mSubtitle: Subtitle? = null
    private var mTracksSelectionMenu: TrackSelectionMenu? = null

    // View from exoplayer for displaying subtitles
    private lateinit var mSubtitleView: SubtitleView
    private var mPlaying = false
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
        mProgress = PlaybackFragmentArgs.fromBundle(requireArguments()).progress
        Timber.tag(Settings.TAG).d("Progress after "+mProgress)
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
        mPlaying = false
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
        Timber.tag(Settings.TAG).d("onStop")
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

        var watchedCall = if(mCurrentVideo.type == "movie")
            trakttvService.setMovieWatched(access_token,tmdbId,date)
        else
            trakttvService.setEpisodeWatched(access_token,tmdbId,date)
        watchedCall.enqueue(object:Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Timber.tag(Settings.TAG).d("TraktTV started")
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Timber.tag(Settings.TAG).d("TraktTV failed")
            }
        })

        mPlaylist.position++;

        if(mPlaylist.videos.size > mPlaylist.position) {
            mPlaylist.videos[mPlaylist.position.toInt()].progress = 0.0f;
            addVideo(mPlaylist.videos[mPlaylist.position.toInt()])
        } else {
            try {
                val navController = findNavController(this.requireView())
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
            val tmdbId = mCurrentVideo.tmdbId.toString()
            val scrobble = ScrobbleData(progress)
            val startCall = videoService.stop(mCurrentVideo.type!!,tmdbId,scrobble)

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
        mPlaying = false;
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
        Timber.tag(Settings.TAG).d("Getting stuff")
        val tmdbId = mCurrentVideo.tmdbId.toString()
        val scrobble = ScrobbleData(mProgress)

        val startCall = videoService.start(mCurrentVideo.type!!,tmdbId,scrobble)
        startCall.enqueue(object:Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Timber.tag(Settings.TAG).d("TraktTV started")
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Timber.tag(Settings.TAG).d("TraktTV failed")
            }
        })
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
            // Fire up faye
            FayeService.subscribe("/torrent/"+video.hash) { message ->
                run {
                    val jsonObj = JSONObject(message)

                    val obj = jsonObj.toMap()
                        //val obj = message?.dataAsMap;
                        val event = obj["event"]
                        Timber.tag(Settings.TAG).d("Torrent message "+event)
                        if(event == "TORRENT_ADDED") {
                            activity?.runOnUiThread {
                                activity?.findViewById<TextView>(R.id.loadingStatus)?.text =
                                    obj["msg"].toString();
                            }
                        }
                        if(event == "TORRENT_BUFFERING") {
                            activity?.runOnUiThread {
                                activity?.findViewById<TextView>(R.id.loadingStatus)?.text =
                                    obj["msg"].toString();
                            }
                        }
                        if(event == "TORRENT_READY") {

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
                                        video.subtitles,
                                        "movie",

                                        video.id,
                                        video.imdbId
                                    )

                                    startVideo(video)
                                }
                            } catch (e:Exception){
                                Timber.tag(Settings.TAG).d("Exception in runon")
                            }

                            // Start playing

                        }
                        if(event == "SUBTITLE_READY") {
                            Timber.tag(Settings.TAG).d("Got subtitles from torrent")
                            activity?.runOnUiThread {
                                activity?.findViewById<TextView>(R.id.loadingStatus)?.text =
                                    "Subtitle downloaded"
                            }
                            var language = ILanguage (
                                -1,
                                "und",
                                "und",
                            "und"

                            )
                            var subtitle = ISubtitle(
                                -1,
                                "Sven",
                                language,
                                "",

                                obj["msg"].toString(),
                                null,
                                prepareSrt(obj["msg"].toString())
                            )
                            video.subtitles.add(subtitle)
                        }
                }
            }
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
        if(mVideoPlayer != null) {
            mVideoPlayer!!.disableInternalSubtitle()
        }
        mSubtitle = null;
        mSubtitleView.setCues(null)
    }

    // Convert subtitle to internal CUE
    fun prepareSrt(srt: String): Subtitle {
        val decoder = OpenSubtitleDecoder()
        return decoder.my_decode(srt.toByteArray(), srt.length, true)
    }

    // Download / select external (srt) subtitle
    fun selectExternalSubtitle(index: Int) {
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
            mSubtitle = mExternalSubtitles[index].subtitle
            mVideoPlayer!!.play()
        }
    }

    fun downloadSubtitle(subtitle: ISubtitle,videoId:Long?,hash:String?) {

        if(subtitle.url != null || subtitle.open_subtitle_id != null) {
            Timber.tag(Settings.TAG).d("Got subtitle url "+subtitle.url);
            Timber.tag(Settings.TAG).d("Video id is "+videoId+" hash "+hash)

            val requestCall = subtitleService.downloadSubtitle(videoId, hash, subtitle)
            requestCall.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        Timber.tag(Settings.TAG).d("Subtitle reponse is ok");
                        val sub = response.body() as String

                        val decoder = OpenSubtitleDecoder()
                        mSubtitle = decoder.my_decode(sub.toByteArray(), sub.length, true)
                        Timber.tag(Settings.TAG).d("mSubtitle length "+mSubtitle?.eventTimeCount)
                        if(
                        mExternalSubtitles.add(
                            ISubtitle(
                                -1,
                                subtitle.url,
                                subtitle.language,
                                subtitle.filename,

                                sub,
                                null,
                                mSubtitle
                            )
                        )) {
                            selectExternalSubtitle(mExternalSubtitles.size-1);
                        } else {
                            Timber.tag(Settings.TAG).d("Failed to add external subtitle")
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Timber.tag(Settings.TAG).d("Failed with subtitle")
                    Timber.tag(Settings.TAG).d(t)
                }
            })
        }

    }

    fun selectExternalSubtitle(lang:String): Boolean {
        if(mExternalSubtitles.size > 0) {
            for ((index, value) in mExternalSubtitles.withIndex()) {
                if(value.language?.code== lang) {
                    selectExternalSubtitle(index);
                    return true;
                }
            }
        }
        return false;
    }

    inner class PlayerEventListener : Player.Listener {
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            Timber.tag(Settings.TAG).d("onTrackChanged")
/*
            if(!mVideoPlayer!!.selectInternalSubtitle("en")) {
                if (!selectExternalSubtitle("en")) {
                    disableSubtitles()
                }
            }

 */
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if(isPlaying && !mPlaying) {
                // Let trakttv know we've started watching a video
                mPlaying = true
                if(mProgress > 0.0f) {
                    mVideoPlayer!!.seekTo(mProgress)
                }
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

