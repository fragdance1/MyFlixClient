package com.fragdance.myflixclient.pages.movies

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.filtercard.FilterCardPresenter
import com.fragdance.myflixclient.components.menu.MenuItemBridgeAdapter
import com.fragdance.myflixclient.components.menu.MenuItemPresenter
import com.fragdance.myflixclient.components.menu.MenuView
import com.fragdance.myflixclient.components.moviecard.MovieCard
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import com.fragdance.myflixclient.components.subtitlemodal.OnMenuItemViewClickedListener
import com.fragdance.myflixclient.utils.MyFlixItemBridgeAdapter
import timber.log.Timber
import kotlin.math.floor

class MoviesPage: Fragment(), OnMenuItemViewClickedListener {
    lateinit var mContext: Context
    lateinit var mRootView: ViewGroup
    lateinit var mMovieAdapter:ArrayObjectAdapter
    lateinit var mMovieGrid:VerticalGridView
    lateinit var mMenu:MenuView
    lateinit var mMenuAdapter:MenuItemBridgeAdapter
    private var mMoviesLoadedReceiver:BroadcastReceiver? = null
    private var mCurrentIndex:Char = '*'
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
        view?.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if(b) {
                Timber.tag(Settings.TAG).d("Got focus")
            } else {
                Timber.tag(Settings.TAG).d("Lost foocus")
            }
        }
    }

    private fun scrollTo(char: String) {
        mMovieGrid.scrollToPosition(Settings.movies.indexOfFirst { it.title.lowercase().startsWith(char.lowercase()) })
    }

    private fun setMovies() {
        if(Settings.movies.isNotEmpty()) {
            mMovieAdapter.clear()
            mMovieAdapter.addAll(0, Settings.movies)
            mMovieAdapter.notifyItemRangeChanged(0, Settings.movies.size)

        }
    }
    private fun setupMovieGrid() {
        mMovieGrid = mRootView.findViewById(R.id.movie_grid_view)
        mMovieAdapter = ArrayObjectAdapter(MovieCardPresenter())
        var movieBridgeAdapter = MyFlixItemBridgeAdapter()
        movieBridgeAdapter.setAdapter(mMovieAdapter)
        mMovieGrid.adapter = movieBridgeAdapter
        //mMovieGrid.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> Timber.tag(Settings.TAG).d("Changing "+(v as MovieCard).mTitle) }
        FocusHighlightHelper.setupBrowseItemFocusHighlight(movieBridgeAdapter,
            1, false);

        if(Settings.movies.isNotEmpty()) {
            setMovies()
        }

        movieBridgeAdapter.setOnFocusChanged { view:View ->
            var index = mMovieGrid.selectedPosition.floorDiv(4)*4
            var title = Settings.movies[index].sorting_title;
            var char = title.first()
            if(char != mCurrentIndex) {
                mCurrentIndex = char;
                var index = mCurrentIndex - 'A'
                if(index < 0) {
                    index = -1;
                }
                index++
                mMenu.selectedPosition = index
                mMenuAdapter.setActive(index)
                Toast.makeText(
                    mContext,
                    char.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
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
    private fun setupFilterView() {
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
    }

    private fun setupMenuView() {
        mMenu = mRootView.findViewById(R.id.menu_view);

        var lrp = MenuItemPresenter()
        mMenuAdapter = MenuItemBridgeAdapter(this)
        var mAdapter = ArrayObjectAdapter(lrp)
        mMenuAdapter.setAdapter(mAdapter)

        mMenu.adapter = mMenuAdapter
        mAdapter.add("#")
        var c = 'A'
        while (c <= 'Z') {
            mAdapter.add(c.toString())
            ++c
        }
        mMenu.setOnKeyInterceptListener(object : BaseGridView.OnKeyInterceptListener {
            override fun onInterceptKeyEvent(event: KeyEvent): Boolean {
                Timber.tag(Settings.TAG).d("Intercept")
                if(event.keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    mMovieGrid.requestFocus()
                    return true
                }
                return false
            }
        })
    }
    private fun setupView() {
        // Set up the filters row
        setupFilterView()
        setupMovieGrid()
        setupMenuView()
        mMovieGrid.requestFocus()

    }

    override fun onMenuItemClicked(item: Any) {
        scrollTo(item.toString())
        Timber.tag(Settings.TAG).d("onMenuItemClicked "+item);
        if(item is TextView) {
            Timber.tag(Settings.TAG).d(item.text.toString())
        }
    }

    override fun onMenuItemSelected(item: Any) {
        scrollTo(item.toString())
    }
}
