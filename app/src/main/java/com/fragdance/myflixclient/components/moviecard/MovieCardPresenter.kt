package com.fragdance.myflixclient.components.moviecard

import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.Presenter
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.ITVShow
import com.squareup.picasso.Picasso

class MovieCardPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val card = MovieCard(parent!!.context,null)
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        val v: MovieCard = viewHolder.view as MovieCard
        if(item is IMovie) {


            v.mTitle.text = item.title
            Picasso.get()
                .load(item.poster)
                .fit()
                .into(v.mPoster)
            v.setOnClickListener {
                val bundle = bundleOf("id" to item.id)

                v.findNavController().navigate(
                    R.id.action_global_movie_details, bundle
                )
            }
        } else if(item is ITVShow) {
            v.mTitle.text = item.title
            Picasso.get()
                .load(item.poster)
                .fit()
                .into(v.mPoster)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}