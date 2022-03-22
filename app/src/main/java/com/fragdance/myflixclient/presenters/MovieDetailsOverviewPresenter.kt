package com.fragdance.myflixclient.presenters

import android.view.ViewGroup
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.Presenter

class MovieDetailsOverviewPresenter(presenter:Presenter,logoPresenter:PosterPresenter):
    FullWidthDetailsOverviewRowPresenter(presenter,logoPresenter)