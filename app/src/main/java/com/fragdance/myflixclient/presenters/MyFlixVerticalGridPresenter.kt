package com.fragdance.myflixclient.presenters

import androidx.leanback.widget.VerticalGridPresenter

class MyFlixVerticalGridPresenter( zoom:Int, v:Boolean) : VerticalGridPresenter(zoom,v) {

    override fun initializeGridViewHolder(vh: ViewHolder?) {
        super.initializeGridViewHolder(vh)
        if(vh is ViewHolder) {
            var gridView = vh.gridView
            gridView.verticalSpacing = 60;
            gridView.horizontalSpacing = 16;
            shadowEnabled = false;
        }
    }
}