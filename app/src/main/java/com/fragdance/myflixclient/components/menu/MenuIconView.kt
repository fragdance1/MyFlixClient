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
            val mHeight = (Settings.WIDTH * 0.036).toInt()
            var params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,mHeight);

            layoutParams = params;
            gravity = Gravity.CENTER

            orientation = VERTICAL
            imageView = ImageView(context);
            params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            params.setMargins((Settings.WIDTH * 0.01).toInt(),0,(Settings.WIDTH*0.01).toInt(),0)
            imageView.layoutParams = params;
            addView(imageView)
        }

        fun setImageResource(id:Int) {
            imageView.setImageResource(id)
        }
}