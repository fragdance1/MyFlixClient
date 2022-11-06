package com.fragdance.myflixclient.components.action_bar

import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.Presenter
import androidx.navigation.findNavController

import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.*
import com.fragdance.myflixclient.pages.persondetails.*


import com.fragdance.myflixclient.services.FayeService
import com.fragdance.myflixclient.services.torrentService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File

open class Action(val name:String) {}

class IVideoAction(name:String,val video:IVideo):Action(name)

class IPersonAction(name:String,val id:String):Action(name)

class IEpisodeAction(name:String,val episode: IEpisode):Action(name)

class ITorrentAction(name:String,val id:Long):Action(name)

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
        val data: Action = item as Action
        v.setText(data.name)
        v.setOnClickListener() {
            var playList = IPlayList()

            if(item is IVideoAction) {
                playList.videos.add(item.video)
                val bundle = bundleOf("playlist" to playList)
                v.findNavController().navigate(R.id.action_global_video_player, bundle)
            } else if(item is IPersonAction) {
                val bundle = bundleOf("id" to item.id);
                v.findNavController().navigate(R.id.action_global_person_movies,bundle)
            } else if(item is ITorrentAction) {

            } else if(item is IEpisodeAction) {
                Timber.tag(Settings.TAG).d("Clicked action "+item.episode.name)
                var playlist = IPlayList()
                if(Settings.playList is ArrayList<IVideo>) {
                    // Get index of item
                    var index = (Settings.playList as ArrayList<IVideo>).indexOfFirst { it.tmdbId == item.episode.id }

                    playlist.videos = (Settings.playList as ArrayList<IVideo>)!!;
                    playlist.position = index.toLong()

                } else {
                    /*
                    playlist.videos.add(item.video)
                    playlist.position = 0;

                     */
                }
                Timber.tag(Settings.TAG).d("Progress before "+item.episode.progress)
                val bundle = bundleOf("playlist" to playlist,"progress" to item.episode.progress)
                v.findNavController().navigate(R.id.action_global_video_player, bundle)
            }
            /*
            if(item.video is IVideo) {

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

             */
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}