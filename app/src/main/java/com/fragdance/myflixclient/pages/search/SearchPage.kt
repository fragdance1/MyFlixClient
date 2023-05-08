package com.fragdance.myflixclient.pages.search

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle

import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieSearchResult

import com.fragdance.myflixclient.services.trakttvService
import timber.log.Timber
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchPage: SearchSupportFragment(),SearchSupportFragment.SearchResultProvider {
    private lateinit var mAdapter: ArrayObjectAdapter
    val movieCardPresenter = MovieCardPresenter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = ArrayObjectAdapter(ListRowPresenter())
        setSearchResultProvider(this);
    }

    override fun onPause() {
        super.onPause()
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return mAdapter;
    }

    override fun onQueryTextChange(newQuery: String?): Boolean {
        Timber.tag(Settings.TAG).d("text changed")
        return true;
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Timber.tag(Settings.TAG).d("text submit");
        loadQuery(query);
        return true
    }

    private fun loadQuery(query:String?) {
        if(query is String) {
            mAdapter.clear()
            Timber.tag(Settings.TAG).d("Asking TMDB about "+query)
            val requestCall = trakttvService.search(query)
            requestCall.enqueue(object : Callback<IMovieSearchResult> {
                override fun onResponse(
                    call: Call<IMovieSearchResult>,
                    response: Response<IMovieSearchResult>
                ) {
                    if (response.isSuccessful) {

                        Timber.tag(Settings.TAG).d("Got some movies"+response.body()!!);
                        val listRowAdapter = ArrayObjectAdapter(movieCardPresenter)
                        listRowAdapter.addAll(0, response.body()!!.results)
                        val header = HeaderItem(0, "Test")
                        mAdapter.add(ListRow(null, listRowAdapter))
                    } else {
                        Timber.tag(Settings.TAG).d("No movies for us")
                    }
                }

                override fun onFailure(call: Call<IMovieSearchResult>, t: Throwable) {
                    Timber.tag(Settings.TAG).d("Abort, abort")
                }

            });
        }
    }

}