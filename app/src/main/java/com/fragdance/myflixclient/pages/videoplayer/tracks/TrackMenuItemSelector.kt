package com.fragdance.myflixclient.pages.videoplayer.tracks

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.fragdance.myflixclient.models.ITrackMenuItem


class TrackMenuItemSelector : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val tv = TextView(parent!!.context)
        tv.focusable = View.FOCUSABLE
        tv.isFocusableInTouchMode = true
        val lp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.START
        tv.layoutParams = lp
        tv.textSize=24f
        tv.setTextColor(Color.WHITE)
        tv.setPadding(20, 4, 0, 4)
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val menuItem = item as ITrackMenuItem
        with(viewHolder?.view as TextView) {
            text = menuItem.label
            if (menuItem.trackId != -1) {
                focusable = View.FOCUSABLE
                setTypeface(typeface, Typeface.NORMAL)
                setPadding(20, 8, 0, 8)
            } else {
                focusable = View.NOT_FOCUSABLE
                setTypeface(typeface, Typeface.BOLD)
                setPadding(20, 20, 0, 4)
            }
        }

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}
