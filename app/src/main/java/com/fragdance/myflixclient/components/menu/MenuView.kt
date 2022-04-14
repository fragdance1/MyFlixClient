package com.fragdance.myflixclient.components.menu

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.View.FOCUSABLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridView
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.subtitlemodal.OnMenuItemViewClickedListener
import com.fragdance.myflixclient.models.ISubtitle
import timber.log.Timber


class MenuView(context: Context,attrs: AttributeSet): VerticalGridView(context,attrs) {
    override fun onFinishInflate() {
        super.onFinishInflate()
        //requestFocus()
    }
}

class MenuItemPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        checkNotNull(parent)
        var label = MenuLabelView(parent.context);

        label.focusable = FOCUSABLE;
        label.isFocusableInTouchMode = true;
        label.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        //label.setTextColor(Color.WHITE)
        val lp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.START
        label.layoutParams = lp

        //label.setTextColor(Color.WHITE)
        label.setPadding(20, 10, 20, 10)

        return ViewHolder(label)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        var label = viewHolder.view as MenuLabelView;
        label.setText((item as ISubtitle).filename)
        label.focusable = FOCUSABLE
        label.isFocusableInTouchMode = true;

    }


    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}

class MenuItemBridgeAdapter(onItemClickListener: OnMenuItemViewClickedListener):ItemBridgeAdapter() {
    var mOnItemClickListener = onItemClickListener

    override fun onCreate(viewHolder: ViewHolder?) {

    }

    override fun onBind(viewHolder: ViewHolder?) {
        var view = viewHolder!!.itemView
        view.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                mOnItemClickListener.onMenuItemClicked(viewHolder.item)
            }
        })
        view.setOnFocusChangeListener { view, b ->

            if(view is MenuLabelView) {
                view.setSelected(b)
            }
        }
    }

    override fun onUnbind(viewHolder: ViewHolder?) {
        super.onUnbind(viewHolder)
    }

    override fun onAttachedToWindow(viewHolder: ViewHolder?) {
        viewHolder?.itemView?.isActivated = true
    }
}
