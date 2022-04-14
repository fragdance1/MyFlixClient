package com.fragdance.myflixclient.presenters

import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.leanback.widget.*
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.action_bar.ActionBar
import com.fragdance.myflixclient.components.action_bar.ActionBarButtonPresenter
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IMovieTorrent
import com.fragdance.myflixclient.models.ITVShow
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.services.torrentService
import com.fragdance.myflixclient.views.MovieDetailsHeroView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.fragdance.myflixclient.utils.movieDetailsToVideo
import timber.log.Timber

data class IAction(
    val name:String,
    val video: IVideo // Can be -1 for not downloaded yet

)


class MovieDetailsHeroPresenter:Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val card = MovieDetailsHeroView(parent!!.context)
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        checkNotNull(viewHolder)
        val v: View = viewHolder.view

        //val mDetails: IMovieDetails = item as IMovieDetails
        //Timber.tag(Settings.TAG).d("Setting backdrop")

        // Load backdrop
        var backdropView: ImageView = v.findViewById(R.id.backdrop)
        var backdropUrl: String? = null
        var posterUrl: String? = null
        var summary: String? = null

        when (item) {
            is IMovieDetails -> {
                if (item.backdrop is String) {
                    backdropUrl =
                        if (item.backdrop!!.startsWith("http")) item.backdrop else Settings.SERVER + item.backdrop
                }
                if (item.poster is String) {
                    posterUrl =
                        if (item.poster!!.startsWith("http")) item.poster else Settings.SERVER + item.poster
                }
                summary = item.overview
            }
            is ITVShow -> {
                if (item.backdrop is String) {
                    backdropUrl =
                        if (item.backdrop!!.startsWith("http")) item.backdrop else Settings.SERVER + item.backdrop
                }
                if (item.poster is String) {
                    posterUrl =
                        if (item.poster!!.startsWith("http")) item.poster else Settings.SERVER + item.poster
                }
                summary = item.overview
            }
        }

        if (backdropUrl != null) {
            Picasso.get()
                .load(backdropUrl)
                .into(backdropView)
        }

        // Load poster
        if (posterUrl != null) {

            var poster: ImageView = v.findViewById(R.id.poster)
            var lp2 = poster.layoutParams as AbsoluteLayout.LayoutParams;
            lp2.width = (Settings.WIDTH * 0.2).toInt()
            lp2.height = (Settings.WIDTH * 0.3).toInt()
            lp2.x = (Settings.WIDTH * 0.1).toInt()
            lp2.y = ((Settings.HEIGHT * 0.3)).toInt()
            Picasso.get()
                .load(posterUrl)
                .into(poster)
        }


        // Set genres text
        var genres: TextView = v.findViewById(R.id.genres)
        var lp: LinearLayout.LayoutParams = genres.layoutParams as LinearLayout.LayoutParams;
        lp.leftMargin = (Settings.WIDTH * 0.32).toInt()
        lp.topMargin = (Settings.WIDTH * 0.015).toInt();
        lp.bottomMargin = (Settings.WIDTH * 0.015).toInt();
        genres.text = "Genres"


        // Summary / synopsis
        if (summary != null) {
            var summaryView: TextView = v.findViewById(R.id.summary)
            lp = summaryView.layoutParams as LinearLayout.LayoutParams;
            summaryView.textSize =
                (Settings.WIDTH * 0.015f) / v.resources.displayMetrics.scaledDensity;
            lp.leftMargin = (Settings.WIDTH * 0.32).toInt()
            lp.rightMargin = 16
            summaryView.text = summary//.overview
        }

        // Actions
        var actionBar: ActionBar = v.findViewById(R.id.movie_details_actions)

        var actions: HorizontalGridView =
            actionBar.getGridView()//v.findViewById(R.id.movie_details_actions)


        var rowsAdapter = ArrayObjectAdapter(createPresenterSelector())


        val actionsAdapter = ArrayObjectAdapter(ActionBarButtonPresenter())
        var actionsRow = ListRow(actionsAdapter)

        rowsAdapter.add(actionsRow)

        var bridgeAdapter = ItemBridgeAdapter();
        bridgeAdapter.setAdapter(rowsAdapter)
        actions.adapter = bridgeAdapter

        if (item is IMovieDetails) {
            if (item.url != null) {
                actionsAdapter.add(IAction("Play", movieDetailsToVideo(item)));
            }

            val getMovieTorrents = torrentService.getMovieTorrents(item.imdb_id)
            getMovieTorrents.enqueue(object : Callback<IMovieTorrent> {
                override fun onResponse(
                    call: Call<IMovieTorrent>,
                    response: Response<IMovieTorrent>
                ) {
                    if (response.isSuccessful) {
                        val torrents: IMovieTorrent = response.body()!!
                        for (torrent in torrents.torrents) {
                            actionsAdapter.add(
                                IAction(
                                    torrent.quality, IVideo(
                                        item.id.toLong(),
                                        "mkv",
                                        torrents.title,
                                        null,
                                        null,

                                        torrent.url,
                                        torrent.hash

                                    )
                                )
                            )
                        }

                    }
                }

                override fun onFailure(call: Call<IMovieTorrent>, t: Throwable) {
                    Toast.makeText(v.context, "Something went wrong $t", Toast.LENGTH_LONG).show()
                }

            })
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        Timber.tag(Settings.TAG).d("MovieDetailsHeroPresenter unbind")
    }

    private fun createPresenterSelector() = ClassPresenterSelector().apply {
        addClassPresenter(
            ListRow::class.java,
            PersonRowPresenter()
        )

    }

}