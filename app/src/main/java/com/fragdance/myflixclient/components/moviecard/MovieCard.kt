package com.fragdance.myflixclient.components.moviecard

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.leanback.widget.BaseCardView
import com.fragdance.myflixclient.R

class MovieCard(context: Context, attrs: AttributeSet?): BaseCardView(context,attrs) {
    var height:Float = 0.0f;
    var width:Float = 0.0f;
    lateinit var poster:ImageView;
    lateinit var title: TextView;
    init {
        buildImageCardView()
    }

    override fun setSelected(selected:Boolean) {
        val overlay:View = findViewById<View>(R.id.movie_card_info);
        var target = height;
        if(selected) target = 0f;
        val mSlidInAnimator = ObjectAnimator.ofFloat(overlay, "translationY", target );
        mSlidInAnimator.setDuration(300);
        mSlidInAnimator.start();
    }
    fun buildImageCardView() {
        focusable = FOCUSABLE;
        isFocusableInTouchMode = true;

        val displayMetrics = context.resources.displayMetrics
        width = (displayMetrics.widthPixels*0.2).toFloat();
        height = (displayMetrics.widthPixels*0.3).toFloat();


        val inflater:LayoutInflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.movie_card,this);
        val overlay:View = findViewById<View>(R.id.movie_card_info);
        val card = findViewById<ViewGroup>(R.id.movie_card)
        card.layoutParams.width =  width.toInt();
        card.layoutParams.height = height.toInt();
        poster = findViewById(R.id.poster);
        title = findViewById(R.id.movie_card_title)
        overlay.translationY = height;
    }


}