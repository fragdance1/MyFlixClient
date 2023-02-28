package com.fragdance.myflixclient.components.moviegrid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import androidx.core.os.bundleOf
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.FocusHighlight.*
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import androidx.navigation.fragment.findNavController
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.R;
import com.fragdance.myflixclient.presenters.MyFlixVerticalGridPresenter
import timber.log.Timber

open class MovieGridFragment(context: Context,attrs: AttributeSet): VerticalGridView(context,attrs){
    override fun onFinishInflate() {
        super.onFinishInflate()
        setNumColumns(4);
        verticalSpacing=30
    }
}