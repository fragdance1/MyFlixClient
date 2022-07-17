package com.fragdance.myflixclient.pages.persondetails

import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.leanback.widget.*
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.*
import com.fragdance.myflixclient.presenters.PersonRowPresenter
import com.squareup.picasso.Picasso
import timber.log.Timber

data class IAction(
    val name:String,
    val video: IVideo?, // Can be -1 for not downloaded yet
    val person:String?
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
            var portrait =
                if (item.id != null) (Settings.SERVER + "/api/portrait/" + item.id) else item.portrait

            Picasso.get()
                .load(portrait)
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