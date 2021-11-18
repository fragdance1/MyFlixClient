package com.fragdance.myflixclient.pages.videoplayer
import java.io.Serializable
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavArgs
import com.fragdance.myflixclient.models.IMovie

data class PlaybackFragmentArgs(val video: IMovie) : NavArgs {
    companion object {
        @JvmStatic
        fun fromBundle(bundle: Bundle): PlaybackFragmentArgs {
            // Simplify since we only have serializable
            bundle.setClassLoader(PlaybackFragmentArgs::class.java.classLoader)
            val movie: IMovie?
            if (bundle.containsKey("video")) {
                movie = bundle.get("video") as IMovie?
            } else {
                throw UnsupportedOperationException(
                    IMovie::class.java.name +
                            " must implement Parcelable or Serializable or must be an Enum."
                )
            }
            if (movie == null) {
                throw IllegalArgumentException("Argument \"video\" is marked as non-null but was passed a null value.")
            }

            return PlaybackFragmentArgs(movie)
        }
    }
}
