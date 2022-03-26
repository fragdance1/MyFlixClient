package com.fragdance.myflixclient.pages.moviedetails

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IPersonDetails

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: ViewHolder,
        item: Any) {
        if(item is IMovieDetails) {
            viewHolder.title.text = item.title
            viewHolder.subtitle.text = "Comedy"
            viewHolder.body.text = item.overview
        } else if(item is IPersonDetails) {
            viewHolder.title.text = item.name
            viewHolder.subtitle.text = "Comedy"
            viewHolder.body.text = item.biography
        }
    }
}