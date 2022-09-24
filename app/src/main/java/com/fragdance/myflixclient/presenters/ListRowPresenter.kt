package com.fragdance.myflixclient.presenters

import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.*
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class MyFlixListRowPresenter(): ListRowPresenter(1) {
    init {
        shadowEnabled = false
        rowHeight = (Settings.HEIGHT * 1).toInt()

    }

    override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder? {
        var vh:RowPresenter.ViewHolder = super.createRowViewHolder(parent)
        if (vh is ListRowPresenter.ViewHolder) {
            //vh.headerViewHolder?. .view.setPadding(0,100,0,100)
            //vh.view.setPadding(0,100,0,100)
           // vh.gridView.setPadding(0,100,0,100)// = 100
            /*
            vh.getGridView().setPadding(
                vh.mPaddingLeft, paddingTop, vh.mPaddingRight,
                paddingBottom
            )

             */
        }

        return vh;
    }

    override fun onBindViewHolder(
        viewHolder: Presenter.ViewHolder?,
        item: Any?,
        payloads: MutableList<Any>?
    ) {
        super.onBindViewHolder(viewHolder, item, payloads)
    }
    override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder?, item: Any?) {
        super.onBindRowViewHolder(holder, item)
        Timber.tag(Settings.TAG).d("onBindRowViewHolder")
        if(holder is ListRowPresenter.ViewHolder) {
            if(holder.headerViewHolder is RowHeaderPresenter.ViewHolder) {
                holder.headerViewHolder.view.setPadding(0,200,0,0)
            }
        }
    }
    override fun initializeRowViewHolder(holder: RowPresenter.ViewHolder?) {
        super.initializeRowViewHolder(holder)

    }
    override fun isUsingDefaultListSelectEffect(): Boolean {
        return false
    }


}