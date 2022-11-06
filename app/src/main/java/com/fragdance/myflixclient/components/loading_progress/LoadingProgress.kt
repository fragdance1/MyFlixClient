package com.fragdance.myflixclient.components.loading_progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fragdance.myflixclient.R

class LoadingProgress():  Fragment() {
    lateinit var mRootView: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.loading_progress, container, false) as ViewGroup
        return mRootView;
    }
}