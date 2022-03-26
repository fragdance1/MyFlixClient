package com.fragdance.myflixclient.components.action_bar

import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.Presenter
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController

import androidx.navigation.fragment.findNavController
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IPlayList
import com.fragdance.myflixclient.pages.moviedetails.MovieDetailsPageDirections

import com.fragdance.myflixclient.presenters.IAction
import timber.log.Timber
import utils.movieDetailsToVideo

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
            //val bundle = bundleOf("playList" to playList)
            playList.videos.add(movieDetailsToVideo(item.details))
            val bundle = bundleOf("playlist" to playList)

            v.findNavController().navigate(
                R.id.action_global_video_player,bundle)
            //v.findNavController().navigate(MovieDetailsPageDirections.actionDetailsToPlayback(playList)


        }


    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}