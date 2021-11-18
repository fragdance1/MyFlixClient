package com.fragdance.myflixclient.pages.videoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class PlaybackErrorFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(Settings.TAG).d("PlaybackErrorFragment.onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.tag(Settings.TAG).d("PlaybackErrorFragment.onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag(Settings.TAG).d("PlaybackErrorFragment.onViewCreated")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag(Settings.TAG).d("PlaybackErrorFragment.onDestroyView")
    }
}