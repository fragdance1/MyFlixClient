package com.fragdance.myflixclient.components.moviecard

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.setMargins

import androidx.leanback.widget.BaseCardView
import com.fragdance.myflixclient.R

class MovieCard(context: Context, attrs: AttributeSet?): BaseCardView(context,attrs) {
    var mHeight:Float = 0.0f
    var mWidth:Float = 0.0f
    lateinit var mPoster:ImageView
    lateinit var mTitle: TextView
    lateinit var mProgress: ProgressBar

    init {
        buildImageCardView()
    }

    override fun setSelected(selected:Boolean) {
        val overlay = findViewById<View>(R.id.movie_card_info)
        var target = mHeight
        if(selected) target = 0f
        val mSlidInAnimator = ObjectAnimator.ofFloat(overlay, "translationY", target )
        mSlidInAnimator.duration = 300
        mSlidInAnimator.start()
    }

    private fun buildImageCardView() {
        focusable = FOCUSABLE
        isFocusableInTouchMode = true

        val displayMetrics = context.resources.displayMetrics
        mWidth = (displayMetrics.widthPixels*0.2).toFloat()
        mHeight = (displayMetrics.widthPixels*0.3).toFloat()


        val inflater:LayoutInflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.movie_card,this)
        val overlay:View = findViewById<View>(R.id.movie_card_info)
        val card = findViewById<ViewGroup>(R.id.movie_card)
        mProgress = findViewById<ProgressBar>(R.id.progressBar)
        card.layoutParams.width =  mWidth.toInt()
        card.layoutParams.height = mHeight.toInt()

        mPoster = findViewById(R.id.poster)
        mTitle = findViewById(R.id.movie_card_title)
        overlay.translationY = mHeight
    }
}