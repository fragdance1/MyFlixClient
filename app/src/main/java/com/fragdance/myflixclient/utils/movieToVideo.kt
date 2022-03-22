package utils

import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IVideo
import timber.log.Timber

fun movieDetailsToVideo(movie: IMovieDetails): IVideo {
    Timber.tag(Settings.TAG).d("Movie "+movie.id+" "+movie.video_files[0].extension+" "+movie.title)
    return IVideo(movie.id,movie.video_files[0].extension,movie.title,movie.poster,movie.overview,movie.url,movie.video_files[0].subtitles)
}