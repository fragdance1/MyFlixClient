package com.fragdance.myflixclient.presenters

import androidx.leanback.widget.ListRowPresenter

class EpisodeRowPresenter(): ListRowPresenter(1) {
    init {
        shadowEnabled = false

    }
    override fun isUsingDefaultListSelectEffect(): Boolean {
        return false
    }


}