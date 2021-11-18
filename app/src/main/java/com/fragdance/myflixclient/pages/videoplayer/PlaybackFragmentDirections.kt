package com.fragdance.myflixclient.pages.videoplayer

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavDirections
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IMovie
import timber.log.Timber
import java.io.Serializable
import java.lang.Exception
import java.lang.UnsupportedOperationException
import kotlin.Int
import kotlin.Suppress
class PlaybackFragmentDirections private constructor() {
    private data class ActionPlaybackFragmentToPlaybackErrorFragment(
        val video: IMovie,
        val error: Exception
    ) : NavDirections {
        override fun getActionId(): Int = R.id.action_playbackFragment_to_playbackErrorFragment

        @Suppress("CAST_NEVER_SUCCEEDS")
        override fun getArguments(): Bundle {
            Timber.tag(Settings.TAG).d("PlaybackFragmentDirections.ActionPlaybackFragmentToPlaybackErrorFragment.getArguments")
            val result = Bundle()
            if (Parcelable::class.java.isAssignableFrom(IMovie::class.java)) {
                result.putParcelable("video", this.video as Parcelable)
            } else if (Serializable::class.java.isAssignableFrom(IMovie::class.java)) {
                result.putSerializable("video", this.video as Serializable)
            } else {
                throw UnsupportedOperationException(IMovie::class.java.name +
                        " must implement Parcelable or Serializable or must be an Enum.")
            }
            if (Parcelable::class.java.isAssignableFrom(Exception::class.java)) {
                result.putParcelable("error", this.error as Parcelable)
            } else if (Serializable::class.java.isAssignableFrom(Exception::class.java)) {
                result.putSerializable("error", this.error as Serializable)
            } else {
                throw UnsupportedOperationException(Exception::class.java.name +
                        " must implement Parcelable or Serializable or must be an Enum.")
            }
            return result
        }
    }

    companion object {
        fun actionPlaybackFragmentToPlaybackErrorFragment(video: IMovie, error: Exception):
                NavDirections = ActionPlaybackFragmentToPlaybackErrorFragment(video, error)

//        public fun actionGlobalSignInFragment(): NavDirections = NavGraphDirections.actionGlobalSignInFragment()
    }
}
