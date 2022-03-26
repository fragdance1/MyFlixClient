package com.fragdance.myflixclient.presenters

import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.leanback.widget.*
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.action_bar.ActionBar
import com.fragdance.myflixclient.components.action_bar.ActionBarButtonPresenter
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.views.MovieDetailsHeroView
import com.squareup.picasso.Picasso
import timber.log.Timber
data class IAction(
    val name:String,
    val details:IMovieDetails // Can be -1 for not downloaded yet

)


class MovieDetailsHeroPresenter:Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val card = MovieDetailsHeroView(parent!!.context)
        val a = 1
        val b = 2
        val max = if(a > b) a else b
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        val v: View = viewHolder.view

        val mDetails: IMovieDetails = item as IMovieDetails
        //Timber.tag(Settings.TAG).d("Setting backdrop")

        // Load backdrop
        var backdrop: ImageView = v.findViewById(R.id.backdrop)
        Picasso.get()
            .load(Settings.SERVER+mDetails!!.backdrop)
            .into(backdrop)

        // Load poster
        var poster:ImageView = v.findViewById(R.id.poster)
        var lp2 = poster.layoutParams as AbsoluteLayout.LayoutParams;
        lp2.width = (Settings.WIDTH * 0.2).toInt()
        lp2.height = (Settings.WIDTH * 0.3).toInt()
        lp2.x = (Settings.WIDTH * 0.1).toInt()
        lp2.y = ((Settings.HEIGHT * 0.3)).toInt()
        Picasso.get()
            .load(Settings.SERVER+mDetails!!.poster)
            .into(poster)

        // Set genres text
        var genres: TextView = v.findViewById(R.id.genres)
        var lp:LinearLayout.LayoutParams = genres.layoutParams as LinearLayout.LayoutParams;
        lp.leftMargin =  (Settings.WIDTH * 0.32).toInt()
        lp.topMargin = (Settings.WIDTH * 0.015).toInt();
        lp.bottomMargin = (Settings.WIDTH * 0.015).toInt();
        genres.text="Genres"




        // Summary / synopsis
        var summary: TextView = v.findViewById(R.id.summary)
        lp = summary.layoutParams as LinearLayout.LayoutParams;
        summary.textSize = (Settings.WIDTH * 0.015f)/v.resources.displayMetrics.scaledDensity;
        lp.leftMargin = (Settings.WIDTH * 0.32).toInt()
        lp.rightMargin = 16
        summary.text=mDetails.overview


        // Actions
        var  actionBar: ActionBar = v.findViewById(R.id.movie_details_actions)

        var actions: HorizontalGridView = actionBar.getGridView()//v.findViewById(R.id.movie_details_actions)


        var rowsAdapter = ArrayObjectAdapter(createPresenterSelector(mDetails!!))

        val actionsAdapter = ArrayObjectAdapter(ActionBarButtonPresenter()).apply {
            add(IAction("Play",mDetails))
            add(IAction("720p",mDetails))
            add(IAction("1080p", mDetails))
            add(IAction("Play again", mDetails))
            add(IAction("Play", mDetails))
            add(IAction("Play again", mDetails))
            add(IAction("720p",mDetails))
            add(IAction("1080p", mDetails))
            add(IAction("Play again", mDetails))
            add(IAction("Play", mDetails))
            add(IAction("Play again", mDetails))
        }


        var actionsRow = ListRow( actionsAdapter)

        rowsAdapter.add(actionsRow)

        var bridgeAdapter = ItemBridgeAdapter();
        bridgeAdapter.setAdapter(rowsAdapter)
        actions.adapter = bridgeAdapter
    }
    private fun createPresenterSelector(movie: IMovieDetails) = ClassPresenterSelector().apply {
        addClassPresenter(
            ListRow::class.java,
            PersonRowPresenter()
        )

    }
    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}