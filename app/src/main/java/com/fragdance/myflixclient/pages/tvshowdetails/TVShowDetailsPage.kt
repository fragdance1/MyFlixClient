package com.fragdance.myflixclient.pages.tvshowdetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.navArgs
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import timber.log.Timber
import androidx.leanback.widget.ItemBridgeAdapter
import com.fragdance.myflixclient.components.episodecard.EpisodeCardPresenter
import com.fragdance.myflixclient.components.subtitlemodal.OnMenuItemViewClickedListener
import com.fragdance.myflixclient.models.*
import com.fragdance.myflixclient.presenters.*
import com.fragdance.myflixclient.services.sonarrService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TVShowDetailsPage : Fragment(), OnMenuItemViewClickedListener {
    lateinit var mContext: Context
    lateinit var mRootView: ViewGroup
    var mDetails: ITVShow? = null
    lateinit var mSeasons: VerticalGridView
    var mThis = this;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.movie_details_view, container, false) as ViewGroup
        return mRootView
    }

    private fun createPresenterSelector(movie: ITVShow) = ClassPresenterSelector().apply {
        addClassPresenter(
            ListRow::class.java,
            EpisodeRowPresenter()
        )
        addClassPresenter(
            ITVShow::class.java,
            MovieDetailsHeroPresenter()
        )
    }

    fun setupView() {

        mSeasons = mRootView.findViewById(R.id.persons);

        var rowsAdapter = ArrayObjectAdapter(createPresenterSelector(mDetails!!))
        rowsAdapter.add(mDetails)

        val requestCall = sonarrService.getSeasons(mDetails!!.id)
        requestCall.enqueue(object:Callback<List<ISeason>>{
            override fun onResponse(call: Call<List<ISeason>>, response: Response<List<ISeason>>) {
                if (response.isSuccessful) {
                    //Settings.tvshows = response.body()!!
                    Timber.tag(Settings.TAG).d("seasons " + response.body())
                    val seasons:List<ISeason>? = response.body()
                    if(seasons is List<ISeason>) {
                        for(season in seasons) {
                            val castAdapter = ArrayObjectAdapter(EpisodeCardPresenter()).apply {
                                for (episode in season.episodes) {
                                    val video:IVideo? = if(episode.video_files.count() > 0) {
                                        val videoFile = episode.video_files[0] as IVideoFile
                                        val url = "/api/video/local?id="+videoFile.id
                                        IVideo(episode.id,videoFile.extension,episode.name,episode.still,episode.overview,url,null)//,videoFile.subtitles)
                                    } else {
                                        null
                                    }
                                    add(
                                        IEpisodeCardData(
                                            episode.id.toString(),
                                            episode.name,
                                            episode.overview,
                                            episode.still,
                                            video
                                        )
                                    )
                                }
                            }
                            var seasonRow = ListRow(HeaderItem(0, season.title?:"Season "+season.seasonNumber), castAdapter)
                            rowsAdapter.add(seasonRow)
                        }
                    }

                } else {
                    //Toast.makeText(this@MainActivity, "Something went wrong ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<ISeason>>, t: Throwable) {

            }

        })

        var bridgeAdapter = ItemBridgeAdapter()//MenuItemBridgeAdapter(mThis);
        bridgeAdapter.setAdapter(rowsAdapter)
        mSeasons.adapter = bridgeAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()

        val args: TVShowDetailsPageArgs by navArgs()
        mDetails = args.show
        Timber.tag(Settings.TAG).d("tv show is "+mDetails)
        setupView()
    }

    override fun onMenuItemClicked(item: Any) {
        Timber.tag(Settings.TAG).d("onMenuItemClicked " + item);
    }


}


