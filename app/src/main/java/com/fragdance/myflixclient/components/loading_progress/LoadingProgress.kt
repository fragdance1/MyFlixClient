package com.fragdance.myflixclient.components.loading_progress

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class LoadingProgress():  Fragment() {
    lateinit var mRootView: ViewGroup
    lateinit var mProgressView: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.loading_progress, container, false) as ViewGroup
        return mRootView;
    }
}