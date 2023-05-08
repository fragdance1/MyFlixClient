package com.fragdance.myflixclient.components.moviecard

import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.Presenter
import androidx.navigation.findNavController
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

    private fun setIcon(v:MovieCard,hasDisc:Boolean, hasHdd:Boolean, watched:Boolean) {
        v.mIcon.visibility = if (hasDisc || hasHdd) View.VISIBLE else View.GONE
        if(hasDisc && hasHdd) {
            if(watched == true) {
                v.mIcon.setImageResource(R.drawable.hdd_disc_watched)
            } else {
                v.mIcon.setImageResource(R.drawable.hdd_disc_unwatched)
            }
        }
        else if(hasDisc) {
            if(watched == true) {
                v.mIcon.setImageResource(R.drawable.disc_watched)
            } else {
                v.mIcon.setImageResource(R.drawable.disc_unwatched)
            }
        } else if(hasHdd) {
            if(watched == true) {
                v.mIcon.setImageResource(R.drawable.hdd_watched)
            } else {
                v.mIcon.setImageResource(R.drawable.hdd_unwatched)
            }
        }
    }
    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        val v: MovieCard = viewHolder.view as MovieCard
        if(item is IMovie) {

            var poster =
                if (item.id != null) (Settings.SERVER + "/api/images/poster/movie/" + item.id) else item.poster

            v.mTitle.text = item.title
            var hasDisc = (item.discs != null && item.discs.isNotEmpty()) ||(item.disc != null && item.disc == true)
            var hasHdd = (item.video_files != null && item.video_files.isNotEmpty()) ||( item.videofile != null && item.videofile == true)
            var watched = item.watched == true

            setIcon(v,hasDisc,hasHdd,watched)
            try {
                Picasso.get()
                    .load(poster)
                    .fit()
                    .into(v.mPoster)
            } catch(e :Exception ) {

            }
                v.setOnClickListener {
                    var bundle = bundleOf("id" to item.id)

                    v.findNavController().navigate(
                        com.fragdance.myflixclient.R.id.action_global_movie_details, bundle
                    )
                }

            if(item.progress == null || item.progress == 0.0f || item.progress == 100.0f) {
                v.mProgress.visibility = View.GONE
            } else {
                v.mProgress.progress = (item.progress!!).toInt()
                v.mProgress.visibility = View.VISIBLE
            }
        } else if(item is IMovieCardData) {
            var poster = if(item.id != null)  (Settings.SERVER+"/api/images/poster/movie/"+item.id) else item.poster
            Timber.tag(Settings.TAG).d(poster)
            v.mTitle.text = item.title
            Picasso.get()
                .load(poster)
                .fit()
                .into(v.mPoster)
            v.setOnClickListener {
                val bundle = bundleOf("id" to item.id)

                v.findNavController().navigate(
                    com.fragdance.myflixclient.R.id.action_global_movie_details, bundle
                )
            }
            var hasDisc =(item.disc != null && item.disc == true)
            var hasHdd = (item.videofile != null && item.videofile == true)
            var watched = item.watched == true

            setIcon(v,hasDisc,hasHdd,watched)
            if(item.progress != null && item.progress!! > 0.0f) {
                v.mProgress.progress = (item.progress!! * 100.0f).toInt()
                v.mProgress.visibility = View.VISIBLE
            } else {
                v.mProgress.visibility = View.GONE
            }
        } else if(item is ITVShow) {
            v.mTitle.text = item.title
            var poster =
                if (item.id != null) (Settings.SERVER + "/api/images/poster/tv/" + item.id) else item.poster
            Picasso.get()
                .load(poster)
                .fit()
                .into(v.mPoster)

                v.mProgress.visibility = View.GONE
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}