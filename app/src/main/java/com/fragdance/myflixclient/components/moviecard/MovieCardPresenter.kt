package com.fragdance.myflixclient.components.moviecard

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.fragdance.myflixclient.models.IMovie
import com.squareup.picasso.Picasso

class MovieCardPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val card = MovieCard(parent!!.context,null)
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        val v: MovieCard = viewHolder.view as MovieCard
        val movie:IMovie = item as IMovie
        v.mTitle.text = movie.title
        Picasso.get()
            .load(movie.poster)
            .fit()
            .into(v.mPoster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}