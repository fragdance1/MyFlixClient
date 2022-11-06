package com.fragdance.myflixclient.components.menu

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.fragdance.myflixclient.Settings

class MenuLabelView : LinearLayout {
    var textView: TextView

    constructor(context: Context) : super(context) {
        val mHeight = (Settings.WIDTH * 0.036).toInt()

        var params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mHeight);

        layoutParams = params;
        gravity = Gravity.CENTER

        orientation = VERTICAL
        textView = TextView(context);
        focusable = View.FOCUSABLE
        isFocusableInTouchMode = true
        textView.textSize = (Settings.WIDTH * 0.015f) / resources.displayMetrics.scaledDensity;
        params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        params.setMargins((Settings.WIDTH * 0.005).toInt(), 0, (Settings.WIDTH * 0.005).toInt(), 0)
        textView.layoutParams =
            params;
        textView.gravity = Gravity.CENTER_VERTICAL
        addView(textView)
    }

    override fun setSelected(selected: Boolean) {
        if (selected) setBackgroundColor(Color.argb(0.1f,1f, 1f, 1f))
        else setBackgroundColor(Color.TRANSPARENT)
    }

    fun setText(text: String) {
        textView.text = text;
    }

    fun setActive(active: Boolean) {
        if (active) setBackgroundColor(Color.rgb(0.2f, 0.2f, 0.2f))
        else setBackgroundColor(Color.TRANSPARENT)
    }

}