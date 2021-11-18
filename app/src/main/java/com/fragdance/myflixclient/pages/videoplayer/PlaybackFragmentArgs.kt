package com.fragdance.myflixclient.pages.videoplayer
import java.io.Serializable
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavArgs
import com.fragdance.myflixclient.models.IMovie

data class PlaybackFragmentArgs(public val video: IMovie) : NavArgs {
    fun toBundle():Bundle {
        val result = Bundle()

        result.putSerializable("video", this.video as Serializable);
        return result;
    }

    public companion object {
        @JvmStatic
        public fun fromBundle(bundle: Bundle): PlaybackFragmentArgs {
            bundle.setClassLoader(PlaybackFragmentArgs::class.java.classLoader)
            val __video : IMovie?
            if (bundle.containsKey("video")) {
                if (Parcelable::class.java.isAssignableFrom(IMovie::class.java) ||
                    Serializable::class.java.isAssignableFrom(IMovie::class.java)) {
                    __video = bundle.get("video") as IMovie?
                } else {
                    throw UnsupportedOperationException(IMovie::class.java.name +
                            " must implement Parcelable or Serializable or must be an Enum.")
                }
                if (__video == null) {
                    throw IllegalArgumentException("Argument \"video\" is marked as non-null but was passed a null value.")
                }
            } else {
                throw IllegalArgumentException("Required argument \"video\" is missing and does not have an android:defaultValue")
            }
            return PlaybackFragmentArgs(__video)
        }
    }
}
