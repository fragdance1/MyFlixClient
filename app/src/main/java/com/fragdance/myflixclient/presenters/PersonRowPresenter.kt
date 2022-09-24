package com.fragdance.myflixclient.presenters

import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.ListRowPresenter
import com.fragdance.myflixclient.Settings

class PersonRowPresenter(): ListRowPresenter(1) {
    init {
        shadowEnabled = false

    }

    override fun isUsingDefaultListSelectEffect(): Boolean {
        return false
    }


}