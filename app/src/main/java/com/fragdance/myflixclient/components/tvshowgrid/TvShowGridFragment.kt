package com.fragdance.myflixclient.components.tvshowgrid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.FocusHighlight.ZOOM_FACTOR_SMALL
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import androidx.navigation.fragment.findNavController
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.R;
import com.fragdance.myflixclient.models.ITVShow

open class TvShowGridFragment : VerticalGridSupportFragment(){
    private lateinit var mAdapter: ArrayObjectAdapter
    private var mTVShowsLoadedReceiver:BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFragment()
        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is ITVShow -> {
                    val bundle = bundleOf("show" to item)

                    findNavController().navigate(
                        R.id.action_global_tvshow_details,bundle)
                }
            }
        }

        // See if movies are already loaded
        if(Settings.tvshows.isNotEmpty()) {
            setTVShows(Settings.tvshows)
        } else {
            try {
                // Let us know when the movies are loaded
                mTVShowsLoadedReceiver = object:BroadcastReceiver() {
                    override fun onReceive(p0: Context?, p1: Intent?) {
                        setTVShows(Settings.tvshows)
                    }
                }
                val filter = IntentFilter()
                filter.addAction("tvshows_loaded")

                requireContext().registerReceiver(mTVShowsLoadedReceiver, filter)
            } catch(e:Exception) {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mTVShowsLoadedReceiver != null) {
            requireContext().unregisterReceiver(mTVShowsLoadedReceiver)
            mTVShowsLoadedReceiver = null
        }
    }

    fun setTVShows(tvshows: List<ITVShow>) {
        mAdapter.clear()
        mAdapter.addAll(0, tvshows)
        mAdapter.notifyItemRangeChanged(0, tvshows.size)
    }

    private fun setupFragment() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR_SMALL, false)
        gridPresenter.numberOfColumns = 4
        setGridPresenter(gridPresenter)

        mAdapter = ArrayObjectAdapter(MovieCardPresenter())
        adapter = mAdapter
    }
}