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

    override fun onTransitionPrepare(): Boolean {
        Timber.tag(Settings.TAG).d("onTransitionPrepare")
        return super.onTransitionPrepare()
    }
    override fun onTransitionStart() {
        Timber.tag(Settings.TAG).d("onTransitionStart");
        super.onTransitionStart()
    }
/*
    override fun setAdapterAndSelection() {
        super.setAdapterAndSelection()
    }

 */
    fun setupMenu() {
        var rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        rowsAdapter.add(ListRow(HeaderItem(0,"Hello"),ArrayObjectAdapter()))
        rowsAdapter.add(ListRow(HeaderItem(1,"Sven"),ArrayObjectAdapter()))
        //verticalGridView.adapter = rowsAdapter
        adapter = rowsAdapter

    }
    override fun onHeaderSelected(viewHolder: RowHeaderPresenter.ViewHolder?, row: Row?) {
        Timber.tag(Settings.TAG).d("Header selected")
    }
}