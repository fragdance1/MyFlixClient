package com.fragdance.myflixclient.components.moviecard

import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Visibility
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieCardData
import com.fragdance.myflixclient.models.ITVShow
import com.squareup.picasso.Picasso
import timber.log.Timber

class MovieCardPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val card = MovieCard(parent!!.context,null)
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        val v: MovieCard = viewHolder.view as MovieCard
        if(item is IMovie) {
            Timber.tag(Settings.TAG).d("IMovie Progress is "+item.progress)
            var poster =
                if (item.id != null) (Settings.SERVER + "/api/poster/" + item.id+"?type=movie") else item.poster

            v.mTitle.text = item.title
            Picasso.get()
                .load(poster)
                .fit()
                .into(v.mPoster)
            v.setOnClickListener {
                val bundle = bundleOf("id" to item.id)

                v.findNavController().navigate(
                    R.id.action_global_movie_details, bundle
                )
            }
            if(item.progress == null || item.progress == 0.0f) {
                v.mProgress.visibility = View.GONE
            } else {
                Timber.tag(Settings.TAG).d("Progress is "+item.progress)
                v.mProgress.progress = (item.progress!! * 100.0f).toInt()
                v.mProgress.visibility = View.VISIBLE
            }
        } else if(item is IMovieCardData) {
            var poster = if(item.id != null)  (Settings.SERVER+"/api/poster/"+item.id+"?type=movie") else item.poster

            v.mTitle.text = item.title
            Picasso.get()
                .load(poster)
                .fit()
                .into(v.mPoster)
            v.setOnClickListener {
                val bundle = bundleOf("id" to item.id)

                v.findNavController().navigate(
                    R.id.action_global_movie_details, bundle
                )
            }

            if(item.progress != null && item.progress!! > 0.0f) {
                v.mProgress.progress = (item.progress!! * 100.0f).toInt()
                v.mProgress.visibility = View.VISIBLE
            } else {
                v.mProgress.visibility = View.GONE
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