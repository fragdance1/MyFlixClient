package com.fragdance.myflixclient.pages.videoplayer.players

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackGlue
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.pages.videoplayer.ProgressTransportControlGlue
import com.fragdance.myflixclient.pages.videoplayer.VideoPlayerFragment
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import timber.log.Timber
import java.lang.Exception
interface IVideoPlayer {
    fun init(context: Context,playerFragment: VideoPlayerFragment)
    fun loadVideo(video: IVideo)
    fun destroy()
    fun currentMs():Long
    fun getTrackSelector():DefaultTrackSelector?

    fun disableInternalSubtitle()

    fun play()
    fun pause()

    fun getProgress():Float
    fun seekTo(progress:Float)

    fun selectInternalSubtitle(lang:String):Boolean
}

class MyFlixMediaPlayer:IVideoPlayer {
    private lateinit var mMediaPlayerGlue: ProgressTransportControlGlue<MediaPlayerAdapter>
    private lateinit var mMediaPlayerAdapter:MediaPlayerAdapter
    var mMediaPlayer: MediaPlayer? = null

    override fun init(
        context: Context,
        playerFragment: VideoPlayerFragment
    ) {
        try {
            var glueHost = VideoSupportFragmentGlueHost(playerFragment)
            mMediaPlayerAdapter = MediaPlayerAdapter(context)

            mMediaPlayerAdapter.setProgressUpdatingEnabled(true)
            mMediaPlayer = mMediaPlayerAdapter.mediaPlayer
            mMediaPlayerGlue = ProgressTransportControlGlue<MediaPlayerAdapter>(context,mMediaPlayerAdapter,playerFragment.onProgressUpdate,playerFragment)
            mMediaPlayerGlue.setHost(glueHost)
            mMediaPlayerGlue.isSeekEnabled = true
            mMediaPlayerGlue.playWhenPrepared()
        } catch(e: Exception) {
            //Timber.tag(Settings.TAG).d(e.message)
        }
    }

    // AVI-videos doesn't have internal subtitles
    override fun selectInternalSubtitle(lang:String):Boolean {
        return false;
    }
    override fun loadVideo(video: IVideo) {
        try {
            mMediaPlayerAdapter.setDataSource(Uri.parse(Settings.SERVER+video.url))
            mMediaPlayerGlue.title = video.title
        } catch(e:Exception) {
            Timber.tag(Settings.TAG).d("Something went wrong "+e.message)
        }
    }

    override fun seekTo(progress:Float) {
        mMediaPlayer!!.seekTo((progress * mMediaPlayer!!.duration / 100).toInt())
    }
    override fun destroy() {
        /* no-op */
        //mMediaPlayer?.release()
        //mMediaPlayer = null;
        //mMediaPlayerAdapter.release();
    }

    override fun currentMs(): Long {
        //Timber.tag(Settings.TAG).d(mMediaPlayer!!.currentPosition.toString())
        return mMediaPlayer!!.currentPosition.toLong()*1000
    }

    override fun getTrackSelector(): DefaultTrackSelector? {
        return null
    }

    override fun disableInternalSubtitle() {
        /* no-op */
    }

    override fun play() {
        mMediaPlayer?.start()
    }

    override fun pause() {
        mMediaPlayer?.pause()
    }

    override fun getProgress() : Float{
        if(mMediaPlayer?.currentPosition != null && mMediaPlayer?.duration != null) {
            return mMediaPlayer?.currentPosition?.toFloat()!! / mMediaPlayer?.duration?.toFloat()!!
        }
        return 0.0f;
    }
}