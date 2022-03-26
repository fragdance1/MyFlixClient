package com.fragdance.myflixclient.components.peroncard

import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.moviecard.MovieCard
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IPersonCardData
import com.squareup.picasso.Picasso
import timber.log.Timber


class PersonCardPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        var card = PersonCard(parent!!.context)
        card.isFocusable = true
        card.isFocusableInTouchMode = true
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        var person = item as IPersonCardData

        val v: PersonCard = viewHolder?.view as PersonCard

        v.mTitle.text = person.title
        v.mSubtitle.text = person.subtitle
        Picasso.get()
            .load(person.portrait)
            .fit()
            .into(v.mPoster)
        v.setOnClickListener() {
            Timber.tag(Settings.TAG).d("Item clicked"+person.title)

        }
        //(viewHolder!!.view as TextView).text = item as String
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}