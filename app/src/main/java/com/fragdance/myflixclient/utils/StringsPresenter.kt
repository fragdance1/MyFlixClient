package com.fragdance.myflixclient.utils

import android.graphics.Color
import android.view.View.FOCUSABLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter

class StringsPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        var tv = TextView(parent!!.context)
        tv.isFocusable = true
        tv.isFocusableInTouchMode = true
        tv.layoutParams = ViewGroup.LayoutParams(200,300)
        tv.setBackgroundColor(Color.BLACK)
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        (viewHolder!!.view as TextView).text = item as String
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}