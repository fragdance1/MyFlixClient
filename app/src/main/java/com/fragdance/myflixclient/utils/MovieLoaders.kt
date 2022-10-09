package com.fragdance.myflixclient.utils

import androidx.leanback.widget.ArrayObjectAdapter
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IMovie

import com.fragdance.myflixclient.services.movieService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MovieLoaders {
    companion object {
        private fun loadMovies(name:String,requestCall:Call<List<IMovie>>,adapter:ArrayObjectAdapter?) {
            requestCall.enqueue(object : Callback<List<IMovie>> {
                override fun onResponse(
                    call: Call<List<IMovie>>,
                    response: Response<List<IMovie>>
                ) {
                    if (response.isSuccessful) {
                        Settings.startMovies[name] = response.body()!!
                        adapter?.addAll(0,Settings.startMovies[name])
                    }
                }
                override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                    Timber.tag(Settings.TAG).d("Get latest movies failed " + t);
                }

            })
        }

        private fun reloadLatestMovies(adapter:ArrayObjectAdapter?) {
            val requestCall = movieService.getLatestMovies()
            loadMovies("Latest",requestCall,adapter)
        }
        private fun loadLatestMovies(adapter:ArrayObjectAdapter) {
            // Check if already downloaded
            if(Settings.startMovies["Latest"] == null) {
                reloadLatestMovies(adapter)
            }
            if(Settings.startMovies["Latest"]!=null) {
                adapter.addAll(0, Settings.startMovies["Latest"])
            }
        }

        private fun reloadRecommendedMovies(adapter:ArrayObjectAdapter?) {
            val requestCall = movieService.getRecommendedMovies()
            loadMovies("Recommended",requestCall,adapter)
        }
        private fun loadRecommendedMovies(adapter:ArrayObjectAdapter) {
            // Check if already downloaded
            if(Settings.startMovies["Recommended"] == null) {
                reloadRecommendedMovies(adapter)
            }
            if(Settings.startMovies["Recommended"] != null) {
                adapter.addAll(0,Settings.startMovies["Recommended"])
            }
        }

        private fun reloadInProgressMovies(adapter:ArrayObjectAdapter?) {
            val requestCall = movieService.getInProgressMovies()
            loadMovies("In Progress",requestCall,adapter)
        }
        private fun loadInProgressMovies(adapter:ArrayObjectAdapter,) {
            // Check if already downloaded
            if(Settings.startMovies["In Progress"] == null) {
                reloadInProgressMovies(adapter)
            }
            if(Settings.startMovies["In Progress"] != null) {
                adapter.addAll(0,Settings.startMovies["In Progress"])
            }
        }

        private fun reloadBoxOfficeMovies(adapter:ArrayObjectAdapter?) {
            val requestCall = movieService.getBoxOfficeMovies()
            loadMovies("BoxOffice",requestCall,adapter)
        }
        private fun loadBoxOfficeMovies(adapter:ArrayObjectAdapter) {
            // Check if already downloaded
            if(Settings.startMovies["BoxOffice"] == null) {
                reloadBoxOfficeMovies(adapter)
            }
            if(Settings.startMovies["BoxOffice"] != null) {
                adapter.addAll(0,Settings.startMovies["BoxOffice"])
            }
        }

        private fun reloadGenre(genre:String,adapter:ArrayObjectAdapter?) {
            val requestCall = movieService.getMoviesByGenre(genre)
            loadMovies(genre,requestCall,adapter)
        }
        private fun loadGenre(genre:String,adapter:ArrayObjectAdapter) {
            // Check if already downloaded
            if(Settings.startMovies[genre] == null) {
                reloadGenre(genre,adapter)
            }
            if(Settings.startMovies[genre] != null) {
                adapter.addAll(0,Settings.startMovies[genre])
            }
        }

        fun loadMovies(type:String,adapter: ArrayObjectAdapter) {
            when(type) {
                "Latest" -> loadLatestMovies(adapter)
                "Recommended" -> loadRecommendedMovies(adapter)
                "Boxoffice" -> loadBoxOfficeMovies(adapter)
                "In Progress" -> loadInProgressMovies(adapter)
                else -> loadGenre(type,adapter)
            }
        }

        fun reloadMovies(type:String,adapter:ArrayObjectAdapter?) {
            when(type) {
                "Latest" -> reloadLatestMovies(adapter)
                "Recommended" -> reloadRecommendedMovies(adapter)
                "Boxoffice" -> reloadBoxOfficeMovies(adapter)
                "In Progress" -> reloadInProgressMovies(adapter)
                else -> reloadGenre(type,adapter)
            }
        }
    }
}