package com.fragdance.myflixclient.pages.persondetails

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.leanback.widget.*
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.action_bar.ActionBar
import com.fragdance.myflixclient.components.action_bar.ActionBarButtonPresenter
import com.fragdance.myflixclient.models.*
import com.fragdance.myflixclient.presenters.IAction
import com.fragdance.myflixclient.presenters.PersonRowPresenter
import com.fragdance.myflixclient.services.torrentService
import com.fragdance.myflixclient.views.MovieDetailsHeroView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.fragdance.myflixclient.utils.movieDetailsToVideo
import timber.log.Timber

data class IAction(
    val name:String,
    val video: IVideo // Can be -1 for not downloaded yet

)


class PersonDetailsHeroPresenter:Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        Timber.tag(Settings.TAG).d("Create person details presenter")
        val card = PersonDetailsHeroView(parent!!.context)
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        val v: View = viewHolder.view

        //val mDetails: IMovieDetails = item as IMovieDetails
        //Timber.tag(Settings.TAG).d("Setting backdrop")

        // Load backdrop

        var portraitView:ImageView = v.findViewById(R.id.portrait)
        var nameView:TextView = v.findViewById(R.id.name)
        var biographyView:TextView = v.findViewById(R.id.biography)
        var backdropUrl: String? = null
        var posterUrl: String? = null
        var summary: String? = null
        Timber.tag(Settings.TAG).d("Person "+item)
        if(item is IPersonDetails) {
            Picasso.get()
                .load(item.portrait)
                .into(portraitView)
nameView.text = item.name
            biographyView.text = item.biography
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        Timber.tag(Settings.TAG).d("MovieDetailsHeroPresenter unbind")
    }

    private fun createPresenterSelector() = ClassPresenterSelector().apply {
        addClassPresenter(
            ListRow::class.java,
            PersonRowPresenter()
        )

    }

}