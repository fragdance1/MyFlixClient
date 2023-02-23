package com.fragdance.myflixclient.components.filtercard

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.fragdance.myflixclient.MainActivity
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.models.IFilter

class FilterCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        var card = FilterCard(parent!!.context)

        card.isFocusable = true
        card.isFocusableInTouchMode = true
        card.setBackgroundColor(Color.TRANSPARENT)
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        if (item is IFilter) {
            val v: FilterCard = viewHolder?.view as FilterCard
            v.mTitle.text = item.name
            v.setOnClickListener() {
                MainActivity.filterMovies(item.id)
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}