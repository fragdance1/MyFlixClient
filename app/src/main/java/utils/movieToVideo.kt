package utils

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IVideo

fun movieToVideo(movie: IMovie): IVideo {
    return IVideo(movie.id,movie.video_files[0].extension,movie.title,movie.poster,movie.overview,movie.url,movie.video_files[0].subtitles)
}