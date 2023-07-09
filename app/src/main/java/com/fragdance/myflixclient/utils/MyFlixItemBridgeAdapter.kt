package com.fragdance.myflixclient.utils

import android.view.View
import android.view.ViewGroup
import androidx.leanback.transition.TransitionHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.ItemBridgeAdapterShadowOverlayWrapper
import androidx.leanback.widget.ShadowOverlayHelper
import com.fragdance.myflixclient.Settings
import timber.log.Timber


class MyFlixItemBridgeAdapter() : ItemBridgeAdapter() {
    var mShadowOverlayHelper: ShadowOverlayHelper? = null
    private var mShadowOverlayWrapper: Wrapper? = null
    private var mOnFocusChanged:((view: View)->Unit)? = null
    override fun onCreate(viewHolder: ViewHolder) {
        if (mShadowOverlayHelper != null) {
            mShadowOverlayHelper!!.onViewCreated(viewHolder.itemView)
        }
    }

    fun setOnFocusChanged(onFocusChanged:((view:View)->Unit)) {
        mOnFocusChanged = onFocusChanged
    }
    override fun onBind(viewHolder: ViewHolder?) {
        super.onBind(viewHolder)
        if(mShadowOverlayHelper == null) {
            if (mShadowOverlayHelper == null) {
                mShadowOverlayHelper = ShadowOverlayHelper.Builder()
                    .needsOverlay(false)
                    .needsShadow(true)
                    .needsRoundedCorner(false)
                    .preferZOrder(true)
                    .keepForegroundDrawable(true)
                    .options(ShadowOverlayHelper.Options.DEFAULT)
                    .build(viewHolder!!.itemView.context);

                if (mShadowOverlayHelper!!.needsWrapper()) {
                    mShadowOverlayWrapper = ItemBridgeAdapterShadowOverlayWrapper(
                            mShadowOverlayHelper);
                }
            }
        }
        wrapper = mShadowOverlayWrapper

        var view = viewHolder!!.itemView

        view.setOnFocusChangeListener { view, b ->
            if(b) {

                view.scaleX=1.1f//setBackgroundColor(Color.RED)//.setSelected(b)
                view.scaleY=1.1f
                mShadowOverlayHelper!!.setShadowFocusLevel(view,1.0f)
                mOnFocusChanged?.invoke(view)
            } else {
                view.scaleX=1.0f
                view.scaleY=1.0f;
                mShadowOverlayHelper!!.setShadowFocusLevel(view,0.0f)
                //Timber.tag(Settings.TAG).d("Blur "+view.javaClass.name)
            }
        }
    }
}