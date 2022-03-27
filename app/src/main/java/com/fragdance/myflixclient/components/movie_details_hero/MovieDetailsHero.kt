package com.fragdance.myflixclient.components.movie_details_hero

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.AbsoluteLayout
import android.widget.FrameLayout
import com.fragdance.myflixclient.R

class MovieDetailsHero(context: Context, attrs: AttributeSet?): AbsoluteLayout(context,attrs) {
    init {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.movie_details_hero,this)
    }
}