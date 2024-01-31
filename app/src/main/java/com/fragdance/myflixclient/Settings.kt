package com.fragdance.myflixclient

import com.fragdance.myflixclient.models.IFilter
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.ITVShow
import com.fragdance.myflixclient.models.IVideo

class Settings {
    companion object {
        // Address to the backend server. Should be set in settings later on
        var SERVER: String = "127.0.0.1:8000"//http://192.168.1.79:8000"//"http://192.168.1.79:5001";//"http://10.10.10.233:5001"
        var SERVER_IP: String = "127.0.0.1"

        // Just for debug logs
        const val TAG: String = "MyFlixTag"

        // Width/height of the screent
        var WIDTH: Float = 1920.0f
        var HEIGHT: Float = 1080.0f

        /* All the local movies */
        var movies: List<IMovie> = listOf()

        // All the movie filters
        var movieFilters:List<IFilter> = listOf()
        /* All the local tv-shows */
        var tvshows: List<ITVShow> = listOf()

        var startMovies: MutableMap<String, List<IMovie>> = mutableMapOf()

        val MOVIE_GENRES = arrayOf(
            "Action",
            "Adventure",
            "Animation",
            "Biography",
            "Comedy",
            "Crime",
            "Documentary",
            "Drama",
            "Family",
            "Fantasy",
            "History",
            "Horror",
            "Music",
            "Musical",
            "Mystery",
            "Romance",
            "Sci-Fi",
            "Sport",
            "Thriller",
            "War",
            "Western"
        )
        var playList: List<IVideo>? = null
    }
}