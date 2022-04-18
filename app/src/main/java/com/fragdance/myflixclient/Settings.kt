package com.fragdance.myflixclient

import android.os.Handler
import android.os.Looper
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.ITVShow

class Settings {
    companion object {
        // Address to the backend server. Should be set in settings later on
        var SERVER:String = "http://192.168.2.80:5001";//"http://10.10.10.233:5001"
        // Just for debug logs
        const val TAG:String = "MyFlix"

        // Width/height of the screent
        var WIDTH:Float = 0.0f
        var HEIGHT:Float = 0.0f

        /* All the local movies */
        var movies:List<IMovie> = listOf()
        var tvshows:List<ITVShow> = listOf()
        var boxoffice:List<IMovie> = listOf()

        var startMovies:MutableMap<String,List<IMovie>> = mutableMapOf<String,List<IMovie>>()

        val MOVIE_GENRES = arrayOf<String>(
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

        val mainThreadHandler = Handler(Looper.getMainLooper())
    }
}