package utils

import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IVideo
import timber.log.Timber

// Get the next video in the list (movies for now)
fun getNextVideo(current: IVideo):IVideo? {
    for(index in 0 until Settings.movies.size) {
        if(Settings.movies[index].id == current.id) {
            Timber.tag(Settings.TAG).d("Found index "+index)
            return movieToVideo(Settings.movies[index+1])
        }
    }
    return null
}