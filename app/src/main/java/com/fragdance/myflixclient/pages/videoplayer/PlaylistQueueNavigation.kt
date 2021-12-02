package com.fragdance.myflixclient.pages.videoplayer

import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IVideo
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import timber.log.Timber

class PlaylistQueueNavigation(mediaSession:MediaSessionCompat,video: IVideo):
    MediaSessionConnector.QueueNavigator {
    override fun onCommand(
        player: Player,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSupportedQueueNavigatorActions(player: Player): Long {
        Timber.tag(Settings.TAG).d("getSupportedQueueNavigatorActions")
        return PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    }

    override fun onTimelineChanged(player: Player) {
        Timber.tag(Settings.TAG).d("onTimelineChanged")
    }

    override fun getActiveQueueItemId(player: Player?): Long {
        Timber.tag(Settings.TAG).d("getActiveQueueItemId")
        return 1
    }

    override fun onSkipToPrevious(player: Player) {
        Timber.tag(Settings.TAG).d("onSkipToPrevious")
    }

    override fun onSkipToQueueItem(player: Player, id: Long) {
        Timber.tag(Settings.TAG).d("onSkipToQueueItem")
    }

    override fun onSkipToNext(player: Player) {
        Timber.tag(Settings.TAG).d("onSkipToNext")
    }

}