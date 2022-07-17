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
import com.fragdance.myflixclient.models.ITorrentStatus
import com.fragdance.myflixclient.models.IVideo

import com.fragdance.myflixclient.pages.persondetails.IAction
import com.fragdance.myflixclient.services.FayeService
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
            if(item.video is IVideo) {
                /*
                if (item.video?.hash != null) {
                    // Clicked a torrent
                    val addMovieTorrentCall =
                        torrentService.addMovieTorrent(item.video.url!!, item.video.id,
                            item.video.hash!!
                        )
                    FayeService.subscribe("/torrent/"+item.video.hash!!) { message ->
                        run {

                            Timber.tag(Settings.TAG).d("Message "+message)
                        }
                    }
                    addMovieTorrentCall.enqueue(object : Callback<ITorrentStatus> {
                        override fun onResponse(
                            call: Call<ITorrentStatus>,
                            response: Response<ITorrentStatus>
                        ) {
                           val url =
                                "/api/video/torrent/" + item.video.hash
                            if (item.video is IVideo) {
                                val video = IVideo(
                                    item.video.id,
                                    "mkv",
                                    item.video.title,
                                    item.video.poster,
                                    item.video.overview,
                                    url,
                                    item.video.hash,
                                    emptyList(),

                                    item.video.type,
                                    item.video.tmdbId,
                                    item.video.imdbId
                                )

                                playList.videos.add(video)
                                val bundle = bundleOf("playlist" to playList)

                                v.findNavController().navigate(
                                    R.id.action_global_video_player, bundle
                                )
                            }
                        }

                        override fun onFailure(call: Call<ITorrentStatus>, t: Throwable) {
                            Timber.tag(Settings.TAG).d("ActionButton exception " + t)
                        }

                    })
                } else {

                 */
                    if (item.video is IVideo) {
                        playList.videos.add(item.video)
                        val bundle = bundleOf("playlist" to playList)
                        v.findNavController().navigate(R.id.action_global_video_player, bundle)
                    //}
                }
            } else if(item.person is String) {
                Timber.tag(Settings.TAG).d("Clicked more "+item.person);
                val bundle = bundleOf("id" to item.person);
                v.findNavController().navigate(R.id.action_global_person_movies,bundle)
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}