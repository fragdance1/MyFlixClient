package com.fragdance.myflixclient.components.episodecard

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class EpisodeCard(context: Context): BaseCardView(context) {
    var mHeight:Float = 0.0f
    var mWidth:Float = 0.0f
    lateinit var mPoster: ImageView
    lateinit var mTitle: TextView
    lateinit var mSubtitle:TextView
    init {
        buildImageCardView()
    }
    override fun setSelected(selected:Boolean) {
        if(selected) {
            setBackgroundColor(Color.argb(0.01f,1f,1f,1f))
        } else {
            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun buildImageCardView() {
        focusable = FOCUSABLE
        isFocusableInTouchMode = true
        setBackgroundColor(Color.TRANSPARENT)

        val inflater: LayoutInflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.episode_card,this)


        mPoster = findViewById(R.id.portrait)

        mTitle = findViewById(R.id.card_title)
        mSubtitle = findViewById(R.id.card_subtitle)

    }
}