package com.fragdance.myflixclient.pages.videoplayer

import android.content.Context
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class ProgressTransportControlGlue<T : PlayerAdapter>(
    context: Context,
    impl: T,
    private val updateProgress: () -> Unit,
    private val videoPlayer: VideoPlayerFragment
) : PlaybackTransportControlGlue<T>(context, impl) {
    var restart = PlaybackControlsRow.SkipPreviousAction(context)
    var next = PlaybackControlsRow.SkipNextAction(context)
    var closedCaption = PlaybackControlsRow.ClosedCaptioningAction(context)

    override fun onCreateSecondaryActions(secondaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreateSecondaryActions(secondaryActionsAdapter)
        secondaryActionsAdapter.apply {
            add(restart)
            add(next)
            add(closedCaption)
        }
    }

    override fun onUpdateProgress() {
        super.onUpdateProgress()
        videoPlayer.onProgressUpdate()
    }

    override fun onActionClicked(action: Action?) {
        //super.onActionClicked(action)
        when (action) {
            restart -> onRestart()
            closedCaption -> onClosedCaption()
            next -> onNext()
            else -> super.onActionClicked(action)
        }
    }

    private fun onRestart() {
        if(playerAdapter.currentPosition < 5000) {
            videoPlayer.prev()
        } else {
            playerAdapter.seekTo(0)
        }
    }

    private fun onNext() {
        //playerAdapter.next()
        videoPlayer.next()
    }
    private fun onClosedCaption() {
        videoPlayer.onClosedCaption()
    }
}