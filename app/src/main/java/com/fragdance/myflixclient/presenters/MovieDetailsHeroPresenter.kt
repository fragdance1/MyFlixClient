package com.fragdance.myflixclient.presenters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.moviecard.MovieCard
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.views.MovieDetailsHeroView
import com.squareup.picasso.Picasso
import timber.log.Timber

class MovieDetailsHeroPresenter:Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val card = MovieDetailsHeroView(parent!!.context)
        Timber.tag(Settings.TAG).d("onCreateViewHolder")
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        val v: View = viewHolder.view
        val mDetails: IMovieDetails = item as IMovieDetails
        Timber.tag(Settings.TAG).d("Setting backdrop")

        var backdrop: ImageView = v.findViewById(R.id.backdrop)
        Picasso.get()
            .load(Settings.SERVER+mDetails!!.backdrop)
            .into(backdrop)


    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}