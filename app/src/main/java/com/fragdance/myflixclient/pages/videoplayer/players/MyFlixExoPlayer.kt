package com.fragdance.myflixclient.pages.videoplayer.players

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import androidx.leanback.app.VideoSupportFragmentGlueHost
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.pages.videoplayer.ProgressTransportControlGlue
import com.fragdance.myflixclient.pages.videoplayer.VideoPlayerFragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.text.Subtitle
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import okhttp3.OkHttpClient
import timber.log.Timber
import com.google.android.exoplayer2.RendererCapabilities
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.RendererCapabilities.AdaptiveSupport
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource

import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.TrackGroup


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
    private val TEXT_FORMAT: Format = Format.Builder().setSampleMimeType(MimeTypes.TEXT_VTT).build()

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

        }

        mExoPlayerGlue.host.isControlsOverlayAutoHideEnabled = true
    }
    private fun createMediaSession() {
        Timber.tag(Settings.TAG).d("createMediaSession "+mContext);
        mMediaSession = MediaSessionCompat(mContext, VideoPlayerFragment.MEDIA_SESSION_TAG)

        // Connect media session to player (exoplayer)
        mMediaSessionConnector = MediaSessionConnector(mMediaSession).apply {
            // Handle playlist navigation
            //setQueueNavigator(PlaylistQueueNavigation(mediaSession,video))
        }
    }
    private val TEXT_CAPABILITIES = FakeRendererCapabilities(C.TRACK_TYPE_TEXT)
    private val SUBTITLE_CAPABILITIES =
        arrayOf<RendererCapabilities>(TEXT_CAPABILITIES)

    fun buildRenderersFactory():RenderersFactory {
        return DefaultRenderersFactory(mContext).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
    }
    override fun init(
        context: Context,
        playerFragment: VideoPlayerFragment,
    ) {
        mContext = context
        mPlayerFragment = playerFragment

        createMediaSession()
        //mViewModel.addPlaybackStateListener(uiPlaybackStateListener)

        val dataSourceFactory = DefaultDataSource.Factory(
            mContext,
            DefaultHttpDataSource.Factory()
        )

        mTrackSelector = DefaultTrackSelector(mContext)

        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)

        val builder = OkHttpClient.Builder()
        val client = builder.build()

        val renderersFactory: RenderersFactory = buildRenderersFactory()

        mExoplayer =
            ExoPlayer.Builder(mContext)
                .setRenderersFactory(renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(mTrackSelector)
                .build()

        with(mExoplayer!!) {
            addListener(mPlayerFragment.PlayerEventListener())
            mMediaSessionConnector.setPlayer(this)
            mMediaSession.isActive = true
            playWhenReady = true
            trackSelectionParameters =
                trackSelectionParameters.buildUpon().setPreferredTextLanguage("en").build();
        }

    }
    override fun seekTo(progress:Float) {
        Timber.tag(Settings.TAG).d("SEtting progress "+progress)
        mExoplayer!!.seekTo((progress * mExoplayer!!.contentDuration * 0.01f).toLong())
    }

    override fun loadVideo(video: IVideo) {
        val url: String = Settings.SERVER + video.url

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setTag(video)
            .build()
        with(mExoplayer!!) {
            addMediaItem(mediaItem)
        }
        mExoplayer!!.prepare()
        prepareGlue(mExoplayer!!)
        mExoPlayerGlue.title = video.title
        Timber.tag(Settings.TAG).d("Progress "+video.progress)

    }

    override fun disableInternalSubtitle() {
        Timber.tag(Settings.TAG).d("disableInternalSubtitle")
        mExoplayer!!.trackSelectionParameters = mExoplayer!!.trackSelectionParameters
            .buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT,true)
            .build()

    }

    override fun play() {
        mExoplayer!!.play()
    }

    override fun pause() {
        mExoplayer!!.pause()
    }

    override fun destroy() {
        Timber.tag(Settings.TAG).d("exo destroy")
        mMediaSession.isActive = false
        mMediaSessionConnector.setPlayer(null)
        mExoplayer?.let {
            // Pause the player to notify listeners before it is released.
            it.pause()
            it.stop()
            it.release()
            mExoplayer = null
        }
        mMediaSession.release()
    }

    override fun selectInternalSubtitle(lang:String):Boolean {
        if (mExoplayer!!.currentTracks.containsType(C.TRACK_TYPE_TEXT)) {
            val formatBuilder: Format.Builder = TEXT_FORMAT.buildUpon()
            val lessRoleFlags = formatBuilder.setRoleFlags(C.ROLE_FLAG_CAPTION).build()


            val trackGroups = arrayOf<TrackGroup>(TrackGroup(lessRoleFlags))
            val trackGroupArray = TrackGroupArray(*trackGroups)
            // Enable text
            mExoplayer!!.trackSelectionParameters = mExoplayer!!.trackSelectionParameters
                .buildUpon()
                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT,false)
                .setPreferredTextLanguage(lang)
                .build()

            var timeline = mExoplayer!!.currentTimeline
            var periodId = MediaSource.MediaPeriodId(timeline.getUidOfPeriod(/* periodIndex= */ 0));

            var trackSelection = mExoplayer!!.trackSelector!!.selectTracks(SUBTITLE_CAPABILITIES,trackGroupArray,periodId,timeline)

            return trackSelection.length > 0
        }
        return false;
    }
    override fun currentMs():Long {
        return mExoplayer!!.currentPosition * 1000
    }
    override fun getTrackSelector(): DefaultTrackSelector? {
        return mTrackSelector
    }

    override fun getProgress():Float {
        return mExoplayer!!.currentPosition.toFloat() /mExoplayer!!.contentDuration.toFloat()
    }

    private class FakeRendererCapabilities(private val trackType: Int) :
        RendererCapabilities {
        override fun getName(): String {
            return "FakeRenderer(" + Util.getTrackTypeString(trackType).toString() + ")"
        }

        override fun getTrackType(): Int {
            return trackType
        }

        @Throws(ExoPlaybackException::class)
        override fun supportsFormat(format: Format): @RendererCapabilities.Capabilities Int {
            return if (MimeTypes.getTrackType(format.sampleMimeType) == trackType) RendererCapabilities.create(
                C.FORMAT_HANDLED,
                RendererCapabilities.ADAPTIVE_SEAMLESS,
                RendererCapabilities.TUNNELING_NOT_SUPPORTED
            ) else RendererCapabilities.create(C.FORMAT_UNSUPPORTED_TYPE)
        }

        @Throws(ExoPlaybackException::class)
        override fun supportsMixedMimeTypeAdaptation(): @AdaptiveSupport Int {
            return RendererCapabilities.ADAPTIVE_SEAMLESS
        }
    }

}