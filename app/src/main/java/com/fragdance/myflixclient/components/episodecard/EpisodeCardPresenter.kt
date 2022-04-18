package com.fragdance.myflixclient.components.episodecard

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.Presenter
import androidx.navigation.findNavController
import com.fragdance.myflixclient.R
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
            Picasso.get()
                .load(item.poster)
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
                    var playList = IPlayList()
                    playList.videos.add(item.video)
                    val bundle = bundleOf("playlist" to playList)

                    v.findNavController().navigate(R.id.action_global_video_player, bundle)
                }


            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}