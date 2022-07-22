package com.fragdance.myflixclient.pages.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class SplashFragment: Fragment() {
    lateinit var mRootView: ViewGroup
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.tag(Settings.TAG).d("SplashFragment.onCreateView")
        mRootView = inflater.inflate(R.layout.splash, container, false) as ViewGroup
        return mRootView
    }
}