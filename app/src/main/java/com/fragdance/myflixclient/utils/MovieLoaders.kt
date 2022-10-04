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
        private fun loadLatestMovies(adapter:ArrayObjectAdapter) {
            // Check if already downloaded
            val movies = Settings.startMovies["Latest"];
            if(movies!=null) {

                adapter.addAll(0, movies)
            } else {
                val requestCall = movieService.getLatestMovies()
                requestCall.enqueue(object : Callback<List<IMovie>> {
                    override fun onResponse(
                        call: Call<List<IMovie>>,
                        response: Response<List<IMovie>>
                    ) {
                        if (response.isSuccessful) {
                            adapter.addAll(0, response.body()!!)
                            Settings.startMovies["Latest"] = response.body()!!
                        }
                    }

                    override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                        Timber.tag(Settings.TAG).d("Get latest movies failed " + t);
                    }

                })
            }
        }
        private fun loadRecommendedMovies(adapter:ArrayObjectAdapter) {
            // Check if already downloaded
            val movies = Settings.startMovies["Recommended"];
            if(movies!=null) {

                adapter.addAll(0, movies)
            } else {
                val requestCall = movieService.getRecommendedMovies()
                requestCall.enqueue(object : Callback<List<IMovie>> {
                    override fun onResponse(
                        call: Call<List<IMovie>>,
                        response: Response<List<IMovie>>
                    ) {
                        if (response.isSuccessful) {
                            adapter.addAll(0, response.body()!!)
                            Settings.startMovies["Recommended"] = response.body()!!
                        }
                    }

                    override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                        Timber.tag(Settings.TAG).d("Recommended movies failed" + t);
                    }

                })
            }
        }
        private fun loadInProgressMovies(adapter:ArrayObjectAdapter) {
            // Check if already downloaded
            val movies = Settings.startMovies["In Progress"];
            if(movies!=null) {
                adapter.addAll(0, movies)
            } else {
                val requestCall = movieService.getInProgressMovies()
                requestCall.enqueue(object : Callback<List<IMovie>> {
                    override fun onResponse(
                        call: Call<List<IMovie>>,
                        response: Response<List<IMovie>>
                    ) {
                        if (response.isSuccessful) {
                            adapter.addAll(0, response.body()!!)
                            Settings.startMovies["In Progress"] = response.body()!!
                        }
                    }

                    override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                        Timber.tag(Settings.TAG).d("In Progress movies failed " + t);
                    }

                })
            }
        }
        private fun loadBoxOfficeMovies(adapter:ArrayObjectAdapter) {
            // Check if already downloaded
            val movies = Settings.startMovies["BoxOffice"];
            if(movies!=null) {

                adapter.addAll(0, movies)
            } else {
                val requestCall = movieService.getBoxOfficeMovies()
                requestCall.enqueue(object : Callback<List<IMovie>> {
                    override fun onResponse(
                        call: Call<List<IMovie>>,
                        response: Response<List<IMovie>>
                    ) {
                        if (response.isSuccessful) {
                            adapter.addAll(0, response.body()!!)
                            Settings.startMovies["BoxOffice"] = response.body()!!
                        }
                    }

                    override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                        Timber.tag(Settings.TAG).d("Boxoffice movies failed " + t);
                    }

                })
            }
        }
        private fun loadGenre(genre:String,adapter:ArrayObjectAdapter) {
            // Check if already downloaded
            val movies = Settings.startMovies[genre];
            if(movies!=null) {

                adapter.addAll(0, movies)
            } else {
                val requestCall = movieService.getMoviesByGenre(genre)
                requestCall.enqueue(object : Callback<List<IMovie>> {
                    override fun onResponse(
                        call: Call<List<IMovie>>,
                        response: Response<List<IMovie>>
                    ) {
                        if (response.isSuccessful) {
                            adapter.addAll(0, response.body()!!)
                            Settings.startMovies[genre] = response.body()!!

                        }
                    }

                    override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                        Timber.tag(Settings.TAG).d("Genre "+genre+" failed " + t);
                    }

                })
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
    }
}