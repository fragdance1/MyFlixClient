package com.fragdance.myflixclient.pages.videoplayer

import android.content.Context
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow

class ProgressTransportControlGlue<T : PlayerAdapter>(
    context: Context,
    impl: T,
    private val updateProgress: () -> Unit,
    private val videoPlayer: VideoPlayerFragment
) : PlaybackTransportControlGlue<T>(context, impl) {
    var restart = PlaybackControlsRow.SkipPreviousAction(context)
    var closedCaption = PlaybackControlsRow.ClosedCaptioningAction(context)

    override fun onCreateSecondaryActions(secondaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreateSecondaryActions(secondaryActionsAdapter)
        secondaryActionsAdapter.apply {
            add(restart)
            add(closedCaption)
        }
    }

    override fun onUpdateProgress() {
        super.onUpdateProgress()
        updateProgress()
    }

    override fun onActionClicked(action: Action?) {
        //super.onActionClicked(action)
        when (action) {
            restart -> onRestart()
            closedCaption -> onClosedCaption()
            else -> super.onActionClicked(action)
        }
    }

    private fun onRestart() {
        playerAdapter.seekTo(0)
    }

    private fun onClosedCaption() {
        videoPlayer.onClosedCaption()
    }
}