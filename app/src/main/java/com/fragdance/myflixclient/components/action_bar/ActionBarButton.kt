package com.fragdance.myflixclient.components.action_bar

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class ActionBarButton : LinearLayout {
    var textView: TextView

    constructor(context: Context) : super(context) {
        setPadding(32, 24, 32, 24)
        textView = TextView(context);
        textView.textSize = (Settings.WIDTH * 0.015f) / resources.displayMetrics.scaledDensity;
        addView(textView)

    }

    override fun setSelected(selected: Boolean) {
        if (selected) setBackgroundColor(Color.rgb(0.2f, 0.2f, 0.2f))
        else setBackgroundColor(Color.TRANSPARENT)
    }


    fun setText(text: String) {
        textView.text = text;
    }


}