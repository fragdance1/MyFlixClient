package com.fragdance.myflixclient.pages.videoplayer

import com.fragdance.myflixclient.models.IVideo

sealed class VideoPlaybackState {
    /** Triggers the state to load the video. */
    data class Load(val video: IVideo) : VideoPlaybackState()
    /** Loading has completed and the player can be prepared. */
    data class Prepare(val video: IVideo, val startPosition: Long) : VideoPlaybackState()
    /** The video has started playback such as playing after Prepare or resuming after Pause. */
    data class Play(val video: IVideo) : VideoPlaybackState()
    /** The video is currently paused. */
    data class Pause(val video: IVideo, val position: Long) : VideoPlaybackState()
    /** The video has ended. */
    data class End(val video: IVideo) : VideoPlaybackState()
    /** Something terribly wrong occurred. */
    data class Error(val video: IVideo, val exception: Exception) : VideoPlaybackState()
}
interface PlaybackStateListener {
    fun onChanged(state: VideoPlaybackState)
    fun onDestroy() {}
}