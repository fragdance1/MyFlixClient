package com.fragdance.myflixclient.pages.videoplayer.players

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.view.ViewGroup
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackGlue
import androidx.navigation.Navigation
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.pages.videoplayer.ProgressTransportControlGlue
import com.fragdance.myflixclient.pages.videoplayer.VideoPlayerFragment
import com.fragdance.myflixclient.pages.videoplayer.tracks.TrackSelectionMenu
import com.fragdance.myflixclient.services.subtitleStringService
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.text.Subtitle
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.common.collect.ImmutableSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception

class MyFlixExoPlayer:IVideoPlayer {
    private lateinit var mExoPlayerGlue: ProgressTransportControlGlue<LeanbackPlayerAdapter>
    private lateinit var mTrackSelector: DefaultTrackSelector
    private lateinit var mContext: Context;
    //private lateinit var mOnProgressUpdate:()-> Unit
    private lateinit var mPlayerFragment:VideoPlayerFragment
    private lateinit var mMediaSession: MediaSessionCompat
    private lateinit var mMediaSessionConnector: MediaSessionConnector
    //private lateinit var mSubtitleView: SubtitleView
    private var mSubtitle: Subtitle? = null
    var mExoplayer: ExoPlayer? = null

    private fun prepareGlue(localExoplayer: ExoPlayer) {
        Timber.tag(Settings.TAG).d("prepareGlue");
        mExoPlayerGlue = ProgressTransportControlGlue(
            mContext,
            LeanbackPlayerAdapter(
                mContext,
                localExoplayer,
                VideoPlayerFragment.PLAYER_UPDATE_INTERVAL_MILLIS.toInt()
            ),

            mPlayerFragment.onProgressUpdate,
            mPlayerFragment
        ).apply {
            host = VideoSupportFragmentGlueHost(mPlayerFragment)
            //title = video.title

            isSeekEnabled = true
            addPlayerCallback(object : PlaybackGlue.PlayerCallback() {
                override fun onPlayCompleted(glue: PlaybackGlue?) {
                    super.onPlayCompleted(glue)
                    mPlayerFragment.onPlayCompleted()

                }
            })
        }
        mExoPlayerGlue.host.isControlsOverlayAutoHideEnabled = true
    }
    private fun createMediaSession() {
        Timber.tag(Settings.TAG).d("createMediaSession");
        mMediaSession = MediaSessionCompat(mContext, VideoPlayerFragment.MEDIA_SESSION_TAG)

        // Connect media session to player (exoplayer)
        mMediaSessionConnector = MediaSessionConnector(mMediaSession).apply {
            // Handle playlist navigation
            //setQueueNavigator(PlaylistQueueNavigation(mediaSession,video))
        }
    }
    override fun init(
        context: Context,
        playerFragment: VideoPlayerFragment,
    ) {
        mContext = context
        mPlayerFragment = playerFragment
        //mOnProgressUpdate = onProgressUpdate
/*
        mSubtitleView = SubtitleView(mContext)
        (playerFragment.view as ViewGroup).addView(mSubtitleView)

 */
        createMediaSession()
        //mViewModel.addPlaybackStateListener(uiPlaybackStateListener)
        /*
        mSubtitleView = SubtitleView(mContext)
        (view as ViewGroup).addView(mSubtitleView)

         */
        /*
        if (mTracksSelectionMenu == null) {
            mTracksSelectionMenu = TrackSelectionMenu()

            childFragmentManager.beginTransaction()
                .replace(R.id.tracks_selection_dock, mTracksSelectionMenu!!)
                .hide(mTracksSelectionMenu!!)
                .commit()
        }

         */
        Timber.tag(Settings.TAG).d("initializePlayer");
        val dataSourceFactory = DefaultDataSource.Factory(
            mContext,
            DefaultHttpDataSource.Factory()
        )

        mTrackSelector = DefaultTrackSelector(mContext)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        mExoplayer =
            ExoPlayer.Builder(mContext)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(mTrackSelector)
                .build()

        with(mExoplayer!!) {
            addListener(mPlayerFragment.PlayerEventListener())
            prepare()
            prepareGlue(this)
            mMediaSessionConnector.setPlayer(this)
            mMediaSession.isActive = true
            playWhenReady = true
        }
        mExoplayer?.prepare()
        mExoplayer?.seekTo(0)
        mExoplayer?.play()
    }

    override fun loadVideo(video: IVideo) {
        val url: String = Settings.SERVER + video.url
        //mExternalSubtitles.clear()
        //mExternalSubtitles.addAll(0, video.subtitles)

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setTag(video)
            .build()
        with(mExoplayer!!) {
            addMediaItem(mediaItem)
        }
    }

    override fun disableInternalSubtitle() {
        mExoplayer!!.trackSelectionParameters = mExoplayer!!.trackSelectionParameters
            .buildUpon()
            .setDisabledTrackTypes(ImmutableSet.of(C.TRACK_TYPE_TEXT))
            .build()
        //mSubtitleView.setCues(null)
    }

    override fun play() {
        mExoplayer!!.play()
    }

    override fun pause() {
        mExoplayer!!.pause()
    }

/*
    private val onProgressUpdate: () -> Unit = {
        if (mSubtitle != null) {
            val cue = mSubtitle!!.getCues(mExoplayer!!.currentPosition * 1000)
            mSubtitleView.setCues(cue)
        }
    }

 */
    override fun disableSubtitles() {
        disableInternalSubtitle()
        mSubtitle = null
        //mSubtitleView.setCues(null)
    }

    override fun selectExternalSubtitle(index: Int) {
        disableInternalSubtitle()
        /*
        if (mExternalSubtitles[index].subtitle == null) {
            mExoplayer!!.pause()
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
                    mExoplayer!!.play()
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    mExoplayer!!.play()
                }
            })
        } else {
            Timber.tag(Settings.TAG).d("Testing")
            mSubtitle = mExternalSubtitles[index].subtitle
            mExoplayer!!.play()
        }

         */
    }

    override fun destroy() {
        mMediaSession.isActive = false
        mMediaSessionConnector.setPlayer(null)
        mExoplayer?.let {
            // Pause the player to notify listeners before it is released.
            it.pause()
            it.release()
            mExoplayer = null
        }
        mMediaSession.release()
    }

    override fun currentMs():Long {
        return mExoplayer!!.currentPosition * 1000
    }
    override fun getTrackSelector(): DefaultTrackSelector? {
        return mTrackSelector
    }

}