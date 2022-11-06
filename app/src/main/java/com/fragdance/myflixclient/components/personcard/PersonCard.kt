package com.fragdance.myflixclient.components.personcard

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.fragdance.myflixclient.R

class PersonCard(context: Context): BaseCardView(context) {
    lateinit var mPoster: ImageView
    lateinit var mTitle: TextView
    lateinit var mSubtitle:TextView
    init {
        buildImageCardView()
    }

    override fun setSelected(selected:Boolean) {
        if(selected) {
            setBackgroundColor(Color.argb(0.1f,1f,1f,1f))
        } else {
            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun buildImageCardView() {
        focusable = FOCUSABLE
        isFocusableInTouchMode = true
        setBackgroundColor(Color.TRANSPARENT)

        val inflater: LayoutInflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.person_card,this)


        mPoster = findViewById(R.id.portrait)

        mTitle = findViewById(R.id.card_title)
        mSubtitle = findViewById(R.id.card_subtitle)
    }
}