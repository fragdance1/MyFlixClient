package com.fragdance.myflixclient.components.menu

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class MenuIconView:LinearLayout {
        var imageView: ImageView;
        constructor(context: Context):super(context) {
            //Timber.tag(Settings.TAG).d("Width "+ Settings.WIDTH);
            val mHeight = (Settings.WIDTH * 0.036).toInt()
            //Timber.tag(Settings.TAG).d("label height "+mHeight)
            var params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,mHeight);

            layoutParams = params;
            gravity = Gravity.CENTER

            orientation = VERTICAL
            imageView = ImageView(context);
            params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            params.setMargins((Settings.WIDTH * 0.01).toInt(),0,(Settings.WIDTH*0.01).toInt(),0)
            imageView.layoutParams = params;//LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)

/*
            textView.textSize = (Settings.WIDTH * 0.015f)/resources.displayMetrics.scaledDensity;

            textView.layoutParams = params;//LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
            textView.gravity = Gravity.CENTER_VERTICAL

 */
            addView(imageView)
        }

        fun setImageResource(id:Int) {
            imageView.setImageResource(id)
        }
        fun setActive(active:Boolean) {
            //if(active) setBackgroundColor(Color.rgb(0.2f,0.2f,0.2f))
            //else setBackgroundColor(Color.TRANSPARENT)
        }
/*
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val mHeight = (Settings.WIDTH * 0.018).toInt()

            layoutParams?.height = mHeight;

    }

 */

}