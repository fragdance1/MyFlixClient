package com.fragdance.myflixclient.utils

import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.models.IVideoFile
import timber.log.Timber

fun movieDetailsToVideo(movie: IMovieDetails): IVideo {
    val videoFile = movie.video_files[0] as IVideoFile
    val url = "/api/video/local/"+videoFile.id
    return IVideo(movie.video_files[0].id.toLong(),movie.video_files[0].extension,movie.title,movie.poster,movie.overview,url,null,movie.video_files[0].subtitles.toMutableList(),"movie",movie.id.toLong(),movie.imdb_id,movie.progress)
}