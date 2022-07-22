package com.fragdance.myflixclient.presenters

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.leanback.widget.*
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.action_bar.ActionBar
import com.fragdance.myflixclient.components.action_bar.ActionBarButtonPresenter
import com.fragdance.myflixclient.models.*
import com.fragdance.myflixclient.pages.persondetails.IAction
import com.fragdance.myflixclient.services.torrentService
import com.fragdance.myflixclient.views.MovieDetailsHeroView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.fragdance.myflixclient.utils.movieDetailsToVideo
import timber.log.Timber




class TvShowDetailsHeroPresenter:Presenter() {
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
        var titleView:TextView = v.findViewById(R.id.title)
        var logoView:ImageView = v.findViewById(R.id.logo)
        var backdropUrl: String? = null
        var posterUrl: String? = null
        var logoUrl:String? = null
        var summary: String? = null
        var title:String? = null
        when (item) {
            is IMovieDetails -> {

                backdropUrl = Settings.SERVER+"/api/backdrop/tv/"+item.id;
                posterUrl = Settings.SERVER+"/api/poster/tv/"+item.id;
                logoUrl = Settings.SERVER+"/api/logo/tv/"+item.id
                summary = item.overview
                title = item.title + (if (item.year != 0) " ("+item.year+")" else "")

            }
            is ITVShow -> {
                Timber.tag(Settings.TAG).d("Is TV")
                backdropUrl = Settings.SERVER+"/api/backdrop/tv/"+item.id
                posterUrl = Settings.SERVER+"/api/poster/tv/"+item.id
                logoUrl = Settings.SERVER+"/api/logo/tv/"+item.id
                title = item.title
                summary = item.overview
            }
        }

        if (backdropUrl != null) {
            Picasso.get()
                .load(backdropUrl)
                .fit()
                .centerCrop(Gravity.TOP)
                .into(backdropView)
        }

        if(logoUrl != null) {
            Picasso.get().load(logoUrl).fit().into(logoView)
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

        lp.bottomMargin = (Settings.WIDTH * 0.015).toInt();

        genres.text = "Genres"
        titleView.text = title
        lp = titleView.layoutParams as LinearLayout.LayoutParams;
        lp.leftMargin = (Settings.WIDTH * 0.32).toInt()
        lp.topMargin = (Settings.WIDTH * 0.015).toInt();
        lp.bottomMargin = (Settings.WIDTH * 0.015).toInt();

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
            actionBar.getGridView()


        var rowsAdapter = ArrayObjectAdapter(createPresenterSelector())


        val actionsAdapter = ArrayObjectAdapter(ActionBarButtonPresenter())
        var actionsRow = ListRow(actionsAdapter)

        rowsAdapter.add(actionsRow)

        var bridgeAdapter = ItemBridgeAdapter();
        bridgeAdapter.setAdapter(rowsAdapter)
        actions.adapter = bridgeAdapter

        if (item is IMovieDetails) {
            //Timber.tag(Settings.TAG).d("details "+item)
            if (item.video_files != null && item.video_files.isNotEmpty()) {
                actionsAdapter.add(IAction("Play", movieDetailsToVideo(item),""));
            }
            Timber.tag(Settings.TAG).d("IMDB id "+item.imdb_id)

            val getMovieTorrents = torrentService.getMovieTorrents(item.imdb_id)
            getMovieTorrents.enqueue(object : Callback<List<ITorrent>> {
                override fun onResponse(
                    call: Call<List<ITorrent>>,
                    response: Response<List<ITorrent>>
                ) {
                    if (response.isSuccessful) {
                        val torrents: List<ITorrent> = response.body()!!
                        for (torrent in torrents) {
                            actionsAdapter.add(
                                IAction(
                                    torrent.quality, IVideo(
                                        item.id.toLong(),
                                        "mkv",
                                        item.title,
                                        null,
                                        null,

                                        torrent.url,
                                        torrent.hash,

                                        emptyList(),
                                        null,
                                        null,
                                        null
                                    ),null
                                )
                            )
                        }

                    }
                }

                override fun onFailure(call: Call<List<ITorrent>>, t: Throwable) {
                    Toast.makeText(v.context, "GetMovieTorrents Something went wrong $t", Toast.LENGTH_LONG).show()
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