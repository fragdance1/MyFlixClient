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
import timber.log.Timber


class MovieGridFragment : VerticalGridSupportFragment(), OnItemViewClickedListener,
    OnItemViewSelectedListener {
    lateinit var adapter: ArrayObjectAdapter;
    var moviesLoadedReceiver:BroadcastReceiver? = null

    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFragment()
        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is IMovie ->
                    findNavController().navigate(
                        MovieGridFragmentDirections.actionBrowseFragmentToPlaybackFragment(item)
                    )
                //is BrowseCustomMenu.MenuItem -> item.handler()
            }
        }

        // See if movies are already loaded
        if(Settings.movies.size > 0) {
            setMovies(Settings.movies);
        } else {
            try {
                // Let us know when the movies are loaded
                moviesLoadedReceiver = object:BroadcastReceiver() {
                    override fun onReceive(p0: Context?, p1: Intent?) {
                        setMovies(Settings.movies);
                    }
                }
                val filter = IntentFilter()
                filter.addAction("movies_loaded")

                requireContext().registerReceiver(moviesLoadedReceiver, filter)
            } catch(e:Exception) {
                Timber.tag(Settings.TAG).d("Failed "+e.message);
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(moviesLoadedReceiver != null) {
            requireContext().unregisterReceiver(moviesLoadedReceiver);
            moviesLoadedReceiver = null;
        }
    }
    fun setMovies(movies: List<IMovie>) {
        adapter.clear();
        adapter.addAll(0, movies);
        adapter.notifyItemRangeChanged(0, movies.size);

    }

    fun setupFragment() {
        var gridPresenter = VerticalGridPresenter(ZOOM_FACTOR_SMALL, false);
        gridPresenter.numberOfColumns = 4;
        setGridPresenter(gridPresenter);
        gridPresenter.onItemViewClickedListener = this;

        adapter = ArrayObjectAdapter(MovieCardPresenter());
        setAdapter(adapter);
    }
/*
    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        val intent = VideoPlaybackActivity.newIntent(requireContext(), item as IMovie)
        startActivity(intent)
    }
*/

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        TODO("Not yet implemented")
    }


}