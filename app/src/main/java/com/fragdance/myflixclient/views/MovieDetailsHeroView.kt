package com.fragdance.myflixclient.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings

class MovieDetailsHeroView(context: Context): LinearLayout(context) {
init {
    val inflater: LayoutInflater = LayoutInflater.from(context)
    inflater.inflate(R.layout.movie_details_hero,this)
    val view = findViewById<ViewGroup>(R.id.movie_details_hero)
    view.layoutParams = LinearLayout.LayoutParams(Settings.WIDTH.toInt(), Settings.HEIGHT.toInt())
}
}