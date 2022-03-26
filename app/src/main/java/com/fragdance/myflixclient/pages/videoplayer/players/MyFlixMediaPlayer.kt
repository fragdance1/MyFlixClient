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

    fun disableSubtitles()
    fun selectExternalSubtitle(index: Int)
    fun disableInternalSubtitle()

    fun play()
    fun pause()
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

            mMediaPlayerAdapter.setProgressUpdatingEnabled(true)//.progressUpdatingInterval = VideoPlayerFragment.PLAYER_UPDATE_INTERVAL_MILLIS.toInt()
            mMediaPlayer = mMediaPlayerAdapter.mediaPlayer
            mMediaPlayerGlue = ProgressTransportControlGlue<MediaPlayerAdapter>(context,mMediaPlayerAdapter,playerFragment.onProgressUpdate,playerFragment)
            mMediaPlayerGlue.setHost(glueHost)
            mMediaPlayerGlue.isSeekEnabled = true
            mMediaPlayerGlue.playWhenPrepared()


        } catch(e: Exception) {
            //Timber.tag(Settings.TAG).d(e.message)
        }
    }

    override fun loadVideo(video: IVideo) {
        try {
            mMediaPlayerAdapter.setDataSource(Uri.parse(Settings.SERVER+video.url))
        } catch(e:Exception) {
            Timber.tag(Settings.TAG).d("Something went wrong "+e.message)
        }
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

    override fun disableSubtitles() {
        /* no-op */
    }

    override fun selectExternalSubtitle(index: Int) {
        TODO("Not yet implemented")
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
}