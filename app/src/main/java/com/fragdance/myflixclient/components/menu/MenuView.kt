package com.fragdance.myflixclient.components.menu

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.View.FOCUSABLE
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridView
import com.fragdance.myflixclient.components.subtitlemodal.OnMenuItemViewClickedListener
import com.fragdance.myflixclient.models.ISubtitle

class MenuView(context: Context,attrs: AttributeSet): VerticalGridView(context,attrs) {

}

class MenuItemPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        checkNotNull(parent)
        var label = MenuLabelView(parent.context);

        label.focusable = FOCUSABLE;
        label.isFocusableInTouchMode = true;
        label.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        val lp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.START
        label.layoutParams = lp

        label.setPadding(20, 10, 20, 10)

        return ViewHolder(label)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        if(item is ISubtitle) {
            var name = "[" + item.language?.english+"] "+item.filename

            var label = viewHolder.view as MenuLabelView;
            label.setText(name)
            label.focusable = FOCUSABLE
            label.isFocusableInTouchMode = true;
        }

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
