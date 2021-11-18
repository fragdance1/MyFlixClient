package com.fragdance.myflixclient.pages.videoplayer

import com.fragdance.myflixclient.models.IMovie

sealed class VideoPlaybackState {
    /** Triggers the state to load the video. */
    data class Load(val video: IMovie) : VideoPlaybackState()
    /** Loading has completed and the player can be prepared. */
    data class Prepare(val video: IMovie, val startPosition: Long) : VideoPlaybackState()
    /** The video has started playback such as playing after Prepare or resuming after Pause. */
    data class Play(val video: IMovie) : VideoPlaybackState()
    /** The video is currently paused. */
    data class Pause(val video: IMovie, val position: Long) : VideoPlaybackState()
    /** The video has ended. */
    data class End(val video: IMovie) : VideoPlaybackState()
    /** Something terribly wrong occurred. */
    data class Error(val video: IMovie, val exception: Exception) : VideoPlaybackState()
}
interface PlaybackStateListener {
    fun onChanged(state: VideoPlaybackState)
    fun onDestroy() {}
}