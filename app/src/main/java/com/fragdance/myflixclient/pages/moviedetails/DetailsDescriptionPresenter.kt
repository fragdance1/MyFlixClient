package com.fragdance.myflixclient.pages.moviedetails

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import android.R
import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.fragdance.myflixclient.models.IMovie
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