package com.fragdance.myflixclient.pages.videoplayer.tracks

import android.graphics.Color
import android.widget.FrameLayout
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView

class TrackMenuPresenter : VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false) {
    var gridView: VerticalGridView? = null
    private var previousSelected: Int = 0
    override fun initializeGridViewHolder(vh: ViewHolder?) {
        super.initializeGridViewHolder(vh)
        gridView = vh!!.gridView as VerticalGridView
        with(gridView as VerticalGridView) {
            setColumnWidth(width)
            horizontalSpacing = 0
            verticalSpacing = 0
            val top = 20 //this is the new value for top padding

            val bottom: Int = paddingBottom

            setPadding(0, top, 0, bottom)

            setOnChildSelectedListener { _, _, position, _ ->
                try {
                    getChildAt(previousSelected).setBackgroundColor(Color.TRANSPARENT)
                    getChildAt(position)?.setBackgroundColor(Color.rgb(0.1f, 0.1f, 0.1f))
                } catch (e: Exception) {

                }
                previousSelected = position
            }
        }

    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder?, item: Any?) {
        super.onBindViewHolder(viewHolder, item)
        if (viewHolder?.view != null) {
            with(viewHolder.view as VerticalGridView) {
                layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
            }
        }

    }
}
