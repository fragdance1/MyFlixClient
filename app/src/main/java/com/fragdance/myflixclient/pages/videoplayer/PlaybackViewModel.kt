package com.fragdance.myflixclient.pages.videoplayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class PlaybackViewModel(application: Application) :
    AndroidViewModel(application),
    PlaybackStateMachine {
    private val playbackStateListeners = arrayListOf<PlaybackStateListener>()
    fun addPlaybackStateListener(listener: PlaybackStateListener) {
        playbackStateListeners.add(listener)
    }

    override fun onStateChange(state: VideoPlaybackState) {
        Timber.tag(Settings.TAG).d("PlaybackViewModel.onStateChange");
        playbackStateListeners.forEach {
            it.onChanged(state)
        }
    }
}