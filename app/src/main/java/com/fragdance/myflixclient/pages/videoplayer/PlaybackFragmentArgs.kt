package com.fragdance.myflixclient.pages.videoplayer
import android.os.Bundle
import androidx.navigation.NavArgs
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IPlayList
import timber.log.Timber


data class PlaybackFragmentArgs(val playlist: IPlayList,val progress:Float) : NavArgs {
    companion object {
        @JvmStatic
        fun fromBundle(bundle: Bundle): PlaybackFragmentArgs {
            // Simplify since we only have serializable
            bundle.setClassLoader(PlaybackFragmentArgs::class.java.classLoader)
            val playlist: IPlayList?
            var progress: Float?
            //Timber.tag(Settings.TAG).d("fromBundle "+bundle)
            if (bundle.containsKey("playlist")) {
                playlist = bundle.get("playlist") as IPlayList?
            } else {
                throw UnsupportedOperationException(
                    IPlayList::class.java.name +
                            " must implement Parcelable or Serializable or must be an Enum."
                )
            }
            if (playlist == null) {
                throw IllegalArgumentException("Argument \"video\" is marked as non-null but was passed a null value.")
            }
            if(bundle.containsKey("progress")) {
                progress = bundle.get("progress") as Float
            } else {
                progress = 0.0f
            }
            return PlaybackFragmentArgs(playlist,progress)
        }
    }
}
