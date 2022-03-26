package com.fragdance.myflixclient.components.action_bar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.leanback.widget.HorizontalGridView
import com.fragdance.myflixclient.Settings

class ActionBar(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private val mGridView: HorizontalGridView

    /**
     * Returns the HorizontalGridView.
     */
    fun getGridView(): HorizontalGridView {
        return mGridView
    }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(com.fragdance.myflixclient.R.layout.lb_list_row, this)
        mGridView =
            findViewById<View>(com.fragdance.myflixclient.R.id.row_content) as HorizontalGridView
        // since we use WRAP_CONTENT for height in lb_list_row, we need set fixed size to false
        mGridView.setHasFixedSize(false)

        orientation = VERTICAL
        descendantFocusability = FOCUS_AFTER_DESCENDANTS
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // This is super-hacky, but for some reason I can't get the gridview to be fullsize otherwise
        var lp = mGridView.layoutParams as LinearLayout.LayoutParams// .LayoutParams
        mGridView.setPadding(0, 0, 0, 0)
        ((mGridView.getChildAt(0) as ViewGroup?)?.getChildAt(1) as ViewGroup?)?.getChildAt(0)
            ?.setPadding(0, 0, 56, 0)
        lp.leftMargin = (Settings.WIDTH * 0.35).toInt()
        var lp2 = mGridView.getChildAt(0).layoutParams;

        lp2.width = (Settings.WIDTH * 0.65).toInt()//ViewGroup.LayoutParams.MATCH_PARENT;
    }
}
