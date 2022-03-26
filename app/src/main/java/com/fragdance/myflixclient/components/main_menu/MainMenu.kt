package com.fragdance.myflixclient.components.main_menu

import android.os.Bundle
import androidx.leanback.app.HeadersSupportFragment
import androidx.leanback.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.fragdance.myflixclient.Settings
import timber.log.Timber

class MainMenu: HeadersSupportFragment(), HeadersSupportFragment.OnHeaderViewSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnHeaderViewSelectedListener(this)
    }


    override fun setOnHeaderViewSelectedListener(listener: OnHeaderViewSelectedListener?) {
        super.setOnHeaderViewSelectedListener(listener)
    }




    override fun onHeaderSelected(viewHolder: RowHeaderPresenter.ViewHolder?, row: Row?) {
        Timber.tag(Settings.TAG).d("Header selected")
    }
}