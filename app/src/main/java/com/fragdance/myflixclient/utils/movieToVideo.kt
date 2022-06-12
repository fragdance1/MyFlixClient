package com.fragdance.myflixclient.utils

import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IVideo
import timber.log.Timber

fun movieDetailsToVideo(movie: IMovieDetails): IVideo {
    return IVideo(movie.id.toLong(),movie.video_files[0].extension,movie.title,movie.poster,movie.overview,movie.url,null,movie.video_files[0].subtitles,"movie",movie.id.toLong(),movie.imdb_id)
}