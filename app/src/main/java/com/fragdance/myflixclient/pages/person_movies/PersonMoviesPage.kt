package com.fragdance.myflixclient.pages.person_movies

import android.graphics.Color
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IPersonDetails
import com.fragdance.myflixclient.pages.persondetails.PersonDetailsPageArgs
import com.fragdance.myflixclient.presenters.MyFlixVerticalGridPresenter
import com.fragdance.myflixclient.services.personService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class PersonMoviesPage: VerticalGridSupportFragment() {
    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupFragment()
        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is IMovie -> {
                    val bundle = bundleOf("id" to item.id.toString())

                    findNavController().navigate(
                        R.id.action_global_movie_details,bundle)
                }
            }
        }

        val args : PersonMoviesPageArgs by navArgs()

        val requestCall = personService.getMovies(args.id)

        requestCall.enqueue(object: Callback<List<IMovie>> {
            override fun onResponse(call: Call<List<IMovie>>, response: Response<List<IMovie>>) {
                if (response.isSuccessful) {
                    Timber.tag(Settings.TAG).d("Got response")
                    setMovies(response.body()!!)
                    //mDetails = response.body()!!

                    //setupView()
                } else {

                }
            }

            override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setMovies(movies: List<IMovie>) {
        mAdapter.clear()
        mAdapter.addAll(0, movies)
        mAdapter.notifyItemRangeChanged(0, movies.size)
    }

    private fun setupFragment() {
        view?.setBackgroundColor(Color.parseColor("#121212"))
        val gridPresenter = MyFlixVerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_SMALL, false)

        gridPresenter.numberOfColumns = 4
        setGridPresenter(gridPresenter)

        mAdapter = ArrayObjectAdapter(MovieCardPresenter())
        setAdapter(mAdapter)
        gridPresenter.focusZoomFactor
    }
}