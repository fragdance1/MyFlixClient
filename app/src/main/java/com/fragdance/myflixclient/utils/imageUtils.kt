package com.fragdance.myflixclient.pages.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

fun loadBitmap(
    activity: Activity,
    imageUrl: String?,
    @DrawableRes defaultImage: Int,
    onLoaded: (bitmap: Bitmap) -> Unit) {
    Glide.with(activity)
        .asBitmap()
        .load(imageUrl)
        .centerCrop()
        .error(defaultImage)
        .into(object: CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                onLoaded(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                placeholder?.let {
                    onLoaded(drawableToBitmap(it))
                }
            }

        })
}

private fun drawableToBitmap (drawable: Drawable): Bitmap  {
    if (drawable is BitmapDrawable) {
        if(drawable.bitmap != null) {
            return drawable.bitmap
        }
    }

    val bitmap = if(drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        // Single color bitmap will be created of 1x1 pixel
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888);
    }

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

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

fun loadBitmapIntoImageView(
    context: Context,
    imageUrl: String?,
    @DrawableRes defaultImage: Int,
    imageView: ImageView
) {
    Glide.with(context)
        .load(imageUrl)
        .centerCrop()
        .error(defaultImage)
        .into(imageView)
}