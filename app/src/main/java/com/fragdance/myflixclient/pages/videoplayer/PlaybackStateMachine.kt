package com.fragdance.myflixclient.pages.videoplayer

interface PlaybackStateMachine {
    fun onStateChange(state: VideoPlaybackState)
}
