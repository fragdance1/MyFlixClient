package com.fragdance.myflixclient.components.personcard

import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.leanback.widget.Presenter
import androidx.navigation.findNavController
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.personcard.PersonCard
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

            val bundle = bundleOf("id" to item.id)

            v.findNavController().navigate(
                R.id.action_global_person_details, bundle
            )

        }
        //(viewHolder!!.view as TextView).text = item as String
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}