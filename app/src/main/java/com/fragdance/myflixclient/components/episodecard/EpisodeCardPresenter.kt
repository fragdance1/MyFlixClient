package com.fragdance.myflixclient.components.episodecard

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.Presenter
import androidx.navigation.findNavController
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IEpisodeCardData
import com.fragdance.myflixclient.models.IPlayList
import com.fragdance.myflixclient.models.IVideo
import com.squareup.picasso.Picasso

class EpisodeCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        var card = EpisodeCard(parent!!.context)
        card.isFocusable = true
        card.isFocusableInTouchMode = true
        card.setBackgroundColor(Color.TRANSPARENT)
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        if (item is IEpisodeCardData) {
            val v: EpisodeCard = viewHolder?.view as EpisodeCard

            v.mTitle.text = item.title
            v.mSubtitle.text = item.subtitle
            if(item.progress > 0) {
                v.mProgress.setProgress((item.progress).toInt())
                v.mProgress.visibility = View.VISIBLE
            } else {
                v.mProgress.visibility = View.GONE
            }
            var still =
                if (item.id != null) (Settings.SERVER + "/api/still/" + item.id) else item.poster
            Picasso.get()
                .load(still)
                .fit()
                .into(v.mPoster)

            var matrix = ColorMatrix()
            if (item.video == null) {
                matrix.setSaturation(0f)
            } else {
                matrix.setSaturation(1f)
            }

            v.mPoster.colorFilter = ColorMatrixColorFilter(matrix)

            v.setOnClickListener() {
                if (item.video is IVideo) {
                    var playlist = IPlayList()
                    if(Settings.playList is ArrayList<IVideo>) {
                        // Get index of item
                            var index = (Settings.playList as ArrayList<IVideo>).indexOfFirst { it.id == item.video.id }

                        playlist.videos = (Settings.playList as ArrayList<IVideo>)!!;
                        playlist.position = index.toLong()
                    } else {
                        playlist.videos.add(item.video)
                        playlist.position = 0;
                    }
                    val bundle = bundleOf("playlist" to playlist)
                    v.findNavController().navigate(R.id.action_global_video_player, bundle)
                }


            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}