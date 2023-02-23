package com.fragdance.myflixclient.components.moviecard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.fragdance.myflixclient.R

class MovieCard(context: Context, attrs: AttributeSet?): BaseCardView(context,attrs) {
    var mHeight:Float = 0.0f
    var mWidth:Float = 0.0f
    lateinit var mPoster:ImageView
    lateinit var mTitle: TextView
    lateinit var mProgress: ProgressBar
    lateinit var mIcon:ImageView
    init {
        buildImageCardView()
    }

    private fun buildImageCardView() {
        focusable = FOCUSABLE
        isFocusableInTouchMode = true
        background.alpha = 0

        mWidth = 240.0f
        mHeight = 360.0f


        val inflater:LayoutInflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.movie_card,this)

        mProgress = findViewById<ProgressBar>(R.id.progressBar)
        mIcon = findViewById<ImageView>(R.id.icon)

        mPoster = findViewById(R.id.poster)

        mTitle = findViewById(R.id.movie_card_title)
    }
}