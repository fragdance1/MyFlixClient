package com.fragdance.myflixclient.pages.movies

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.filtercard.FilterCardPresenter
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import com.fragdance.myflixclient.utils.MyFlixItemBridgeAdapter

class MoviesPage: Fragment() {
    lateinit var mContext: Context
    lateinit var mRootView: ViewGroup
    lateinit var mMovieAdapter:ArrayObjectAdapter
    private var mMoviesLoadedReceiver:BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.movies_view, container, false) as ViewGroup
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
        setupView()
    }

    private fun setMovies() {
        if(Settings.movies.isNotEmpty()) {
            mMovieAdapter.clear()
            mMovieAdapter.addAll(0, Settings.movies)
            mMovieAdapter.notifyItemRangeChanged(0, Settings.movies.size)
        }
    }
    private fun setupView() {
        // Set up the filters row
        val filters: HorizontalGridView = mRootView.findViewById(R.id.movie_filters);
        filters.setNumRows(1)

        filters.horizontalSpacing = 10

        val filterAdapter = ArrayObjectAdapter(FilterCardPresenter()).apply {
            for (filter in Settings.movieFilters) {
                add(filter)
            }
        }

        var bridgeAdapter = MyFlixItemBridgeAdapter()

        FocusHighlightHelper.setupBrowseItemFocusHighlight(bridgeAdapter,
            1, false);

        bridgeAdapter.setAdapter(filterAdapter)

        filters.adapter = bridgeAdapter

        // Set up the movies grid
        val movies:VerticalGridView = mRootView.findViewById(R.id.movie_grid_view)

        mMovieAdapter = ArrayObjectAdapter(MovieCardPresenter())
        var movieBridgeAdapter = MyFlixItemBridgeAdapter()
        movieBridgeAdapter.setAdapter(mMovieAdapter)
        movies.adapter = movieBridgeAdapter
        FocusHighlightHelper.setupBrowseItemFocusHighlight(movieBridgeAdapter,
            1, false);
        if(Settings.movies.isNotEmpty()) {
            mMovieAdapter.clear()
            mMovieAdapter.addAll(0, Settings.movies)
            mMovieAdapter.notifyItemRangeChanged(0, Settings.movies.size)
        }

        mMoviesLoadedReceiver = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                setMovies()
            }
        }
        val filter = IntentFilter()
        filter.addAction("movies_loaded")

        requireContext().registerReceiver(mMoviesLoadedReceiver, filter)
    }

}
