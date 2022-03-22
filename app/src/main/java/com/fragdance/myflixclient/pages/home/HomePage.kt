package com.fragdance.myflixclient.pages.home

import android.os.Bundle
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import com.fragdance.myflixclient.models.IMovie


class HomePageFragment: RowsSupportFragment() {
    fun loadData() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val movieCardPresenter = MovieCardPresenter()
        var videos = arrayListOf<IMovie>()

        val listRowAdapter = ArrayObjectAdapter(movieCardPresenter)
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        listRowAdapter.add(IMovie(-1,"Loading",null,null,"fail"))
        val header = HeaderItem(rowsAdapter.size().toLong(), "Test")
        rowsAdapter.add(ListRow(header, listRowAdapter))
        val header2 = HeaderItem(rowsAdapter.size().toLong(), "Test")
        rowsAdapter.add(ListRow(header2, listRowAdapter))
        adapter = rowsAdapter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //title = "Test"
        //headersState = HEADERS_ENABLED
        loadData()
    }
}