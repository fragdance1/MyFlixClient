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


class HomePage: RowsSupportFragment() {
    val movieCardPresenter = MovieCardPresenter()
    val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    fun loadHomePageMovies(type:String) {
        when(type) {
            "Latest" -> {
                val listRowAdapter = ArrayObjectAdapter(movieCardPresenter)
                MovieLoaders.loadMovies(type,listRowAdapter)
                val header = HeaderItem(rowsAdapter.size().toLong(), type)
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }
            "Recommended" -> {
                val listRowAdapter = ArrayObjectAdapter(movieCardPresenter)
                MovieLoaders.loadMovies(type,listRowAdapter)
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

    }
    fun loadData() {
        loadHomePageMovies("Latest")
        loadHomePageMovies("Recommended")
        loadHomePageMovies("Boxoffice")
        for(genre in Settings.MOVIE_GENRES) {
            loadHomePageMovies(genre)
        }

        adapter = rowsAdapter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
    }
}