package com.fragdance.myflixclient.components.filtercard

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.fragdance.myflixclient.R

class FilterCard(context: Context): BaseCardView(context) {
    lateinit var mPoster: ImageView
    lateinit var mTitle: TextView
    lateinit var mSubtitle:TextView
    lateinit var mProgress:ProgressBar

    init {
        buildEpisodeCardView()
    }

    override fun setSelected(selected:Boolean) {
        if(selected) {
            setBackgroundColor(Color.argb(0.05f,1f,1f,1f))
        } else {
            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun buildEpisodeCardView() {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.filter_card,this)

        mTitle = findViewById(R.id.card_title)
    }
}