package com.fragdance.myflixclient.pages.home

import android.os.Bundle
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import com.fragdance.myflixclient.utils.MovieLoaders
import timber.log.Timber


class HomePage: RowsSupportFragment() {
    private val movieCardPresenter = MovieCardPresenter()
    private var mRowPresenter = ListRowPresenter()
    private lateinit var rowsAdapter:ArrayObjectAdapter;// = ArrayObjectAdapter(ListRowPresenter())

    private fun loadHomePageMovies(type:String) {

        val listRowAdapter = ArrayObjectAdapter(movieCardPresenter)

        MovieLoaders.reloadMovies(type,listRowAdapter)
        val header = HeaderItem(rowsAdapter.size().toLong(), type)
        val row = ListRow(header, listRowAdapter)

        rowsAdapter.add(row)
        /*
        when(type) {
            "Latest" -> {

            }
            "Recommended" -> {
                val listRowAdapter = ArrayObjectAdapter(movieCardPresenter)
                MovieLoaders.loadMovies(type,listRowAdapter)
                val header = HeaderItem(rowsAdapter.size().toLong(), type)
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }
            "In Progress" -> {
                val listRowAdapter = ArrayObjectAdapter(movieCardPresenter)
                MovieLoaders.reloadMovies(type,listRowAdapter)
                val header = HeaderItem(rowsAdapter.size().toLong(), type)
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }
            else -> {
                val listRowAdapter = ArrayObjectAdapter(movieCardPresenter)
                MovieLoaders.loadMovies(type,listRowAdapter)
                val header = HeaderItem(rowsAdapter.size().toLong(), type)
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }
        }

         */

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRowPresenter.shadowEnabled = false;
        
        rowsAdapter = ArrayObjectAdapter(mRowPresenter)
        Timber.tag(Settings.TAG).d("HomePage.OnCreate")
        loadData()
    }
}