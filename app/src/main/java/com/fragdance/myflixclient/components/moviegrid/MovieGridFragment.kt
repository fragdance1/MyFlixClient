package com.fragdance.myflixclient.components.moviegrid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.FocusHighlight.ZOOM_FACTOR_SMALL
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import androidx.navigation.fragment.findNavController
import com.fragdance.myflixclient.Settings


class MovieGridFragment : VerticalGridSupportFragment(){
    private lateinit var adapter: ArrayObjectAdapter
    private var moviesLoadedReceiver:BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFragment()
        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is IMovie ->
                    findNavController().navigate(
                        MovieGridFragmentDirections.actionBrowseFragmentToPlaybackFragment(item)
                    )
            }
        }

        // See if movies are already loaded
        if(Settings.movies.isNotEmpty()) {
            setMovies(Settings.movies)
        } else {
            try {
                // Let us know when the movies are loaded
                moviesLoadedReceiver = object:BroadcastReceiver() {
                    override fun onReceive(p0: Context?, p1: Intent?) {
                        setMovies(Settings.movies)
                    }
                }
                val filter = IntentFilter()
                filter.addAction("movies_loaded")

                requireContext().registerReceiver(moviesLoadedReceiver, filter)
            } catch(e:Exception) {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(moviesLoadedReceiver != null) {
            requireContext().unregisterReceiver(moviesLoadedReceiver)
            moviesLoadedReceiver = null
        }
    }

    fun setMovies(movies: List<IMovie>) {
        adapter.clear()
        adapter.addAll(0, movies)
        adapter.notifyItemRangeChanged(0, movies.size)
    }

    private fun setupFragment() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR_SMALL, false)
        gridPresenter.numberOfColumns = 4
        setGridPresenter(gridPresenter)

        adapter = ArrayObjectAdapter(MovieCardPresenter())
        setAdapter(adapter)
    }
}