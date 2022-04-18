package com.fragdance.myflixclient.components.action_bar

import android.content.Context
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import com.fragdance.myflixclient.Settings

class ActionBarButton : LinearLayout {
    var mTextView: TextView

    constructor(context: Context) : super(context) {
        setPadding(32, 24, 32, 24)
        mTextView = TextView(context);
        mTextView.textSize = (Settings.WIDTH * 0.015f) / resources.displayMetrics.scaledDensity;
        addView(mTextView)
    }

    override fun setSelected(selected: Boolean) {
        if (selected) setBackgroundColor(Color.argb(0.1f, 1f, 1f,1f))
        else setBackgroundColor(Color.TRANSPARENT)
    }

    fun setText(text: String) {
        mTextView.text = text;
    }
}