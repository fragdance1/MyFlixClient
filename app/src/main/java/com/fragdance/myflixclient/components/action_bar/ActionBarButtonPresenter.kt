package com.fragdance.myflixclient.components.action_bar

import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.Presenter
import androidx.navigation.findNavController

import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IPlayList
import com.fragdance.myflixclient.models.ITorrentDetails
import com.fragdance.myflixclient.models.IVideo

import com.fragdance.myflixclient.presenters.IAction
import com.fragdance.myflixclient.services.torrentService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File

class ActionBarButtonPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val card = ActionBarButton(parent!!.context)
        card.focusable = View.FOCUSABLE
        card.isFocusableInTouchMode = true;
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        val v: ActionBarButton = viewHolder.view as ActionBarButton
        val data: IAction = item as IAction
        v.setText(data.name)

        v.setOnClickListener() {
            var playList = IPlayList()
            if(item.video.hash != null) {
                // Clicked a torrent
                val addMovieTorrentCall = torrentService.addMovieTorrent(item.video.url!!,item.video.id)

                addMovieTorrentCall.enqueue(object : Callback<ITorrentDetails> {
                    override fun onResponse(
                        call: Call<ITorrentDetails>,
                        response: Response<ITorrentDetails>
                    ) {
                        val details:ITorrentDetails = response.body()!!
                        val files = details.files.sortedBy { it.length }.reversed()
                        val filename = files[0].name
                        val ext = File(filename).extension

                        val url = "/api/torrent/stream?hash="+details.hash+"&file="+files[0].name

                        val video = IVideo(
                            item.video.id,
                            ext,
                            item.video.title,
                            item.video.poster,
                            item.video.overview,
                            url,
                            details.hash
                        )

                        playList.videos.add(video)
                        val bundle = bundleOf("playlist" to playList)

                        v.findNavController().navigate(
                            R.id.action_global_video_player,bundle)

                    }

                    override fun onFailure(call: Call<ITorrentDetails>, t: Throwable) {
                        Timber.tag(Settings.TAG).d("ActionButton exception "+t)
                    }

                })
            } else {
                // Regular (local) video
                playList.videos.add(item.video)
                val bundle = bundleOf("playlist" to playList)
                v.findNavController().navigate(R.id.action_global_video_player,bundle)
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}