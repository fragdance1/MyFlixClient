package com.fragdance.myflixclient.presenters

import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.fragdance.myflixclient.Settings
import timber.log.Timber


class StringPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        Timber.tag(Settings.TAG).d("StringPresenter.onCreateViewHolder")
        val tv = TextView(parent.context)
        tv.isFocusable = true
        tv.isFocusableInTouchMode = true
        tv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200)
        tv.setBackgroundColor(Color.CYAN)
        tv.setTextColor(Color.BLACK)
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        Timber.tag(Settings.TAG).d("StringPresenter.onBindViewHolder for $item")
        (viewHolder.view as TextView).text = item.toString()
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        Timber.tag(Settings.TAG).d( "StringPresenter.onUnbindViewHolder")
    }
}