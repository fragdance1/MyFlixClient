package com.fragdance.myflixclient.pages.home

import android.os.Bundle
import android.view.View
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import com.fragdance.myflixclient.utils.MovieLoaders

class HomePage: RowsSupportFragment() {
    private val movieCardPresenter = MovieCardPresenter()
    private var mRowPresenter = ListRowPresenter()
    private lateinit var rowsAdapter:ArrayObjectAdapter

    private fun loadHomePageMovies(type:String) {

        val listRowAdapter = ArrayObjectAdapter(movieCardPresenter)

        MovieLoaders.loadMovies(type,listRowAdapter)
        val header = HeaderItem(rowsAdapter.size().toLong(), type)

        val row = ListRow(header, listRowAdapter)

        rowsAdapter.add(row)
    }

    private fun loadData() {
        loadHomePageMovies("Latest")
        loadHomePageMovies("Recommended")
        loadHomePageMovies("Boxoffice")
        loadHomePageMovies("In Progress")
        for(genre in Settings.MOVIE_GENRES) {
            loadHomePageMovies(genre)
        }
        adapter = rowsAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //verticalGridView.verticalSpacing=100;
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRowPresenter.shadowEnabled = true;

        setExpand(true)

        rowsAdapter = ArrayObjectAdapter(mRowPresenter)
        loadData()
    }
}