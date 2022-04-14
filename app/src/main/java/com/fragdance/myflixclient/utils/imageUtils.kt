package com.fragdance.myflixclient.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition




fun loadDrawable(
    activity: Activity,
    imageUrl: String?,
    @DrawableRes defaultImage: Int,
    width: Int,
    height: Int,
    onLoaded: (image: Drawable) -> Unit) {

    Glide.with(activity)
        .load(imageUrl)
        .centerCrop()
        .error(defaultImage)
        .into<CustomTarget<Drawable>>(object : CustomTarget<Drawable>(width, height) {

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                onLoaded(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                placeholder?.let { onLoaded(it) }
            }
        })
}

