package com.fragdance.myflixclient.presenters

import androidx.leanback.widget.VerticalGridPresenter

class MyFlixVerticalGridPresenter( zoom:Int, v:Boolean) : VerticalGridPresenter(1,v) {
    override fun initializeGridViewHolder(vh: ViewHolder?) {
        super.initializeGridViewHolder(vh)
        if(vh is ViewHolder) {
            var gridView = vh.gridView
            gridView.verticalSpacing = 40;
            gridView.horizontalSpacing = 20;
        }
    }
}