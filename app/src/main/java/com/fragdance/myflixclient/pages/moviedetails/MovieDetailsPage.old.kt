package com.fragdance.myflixclient.pages.moviedetails

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.moviegrid.MovieGridFragmentDirections

import com.fragdance.myflixclient.components.peroncard.PersonCardPresenter
import com.fragdance.myflixclient.models.*
import com.fragdance.myflixclient.pages.persondetails.PersonDetailsPageArgs

import com.fragdance.myflixclient.pages.utils.loadBitmap
import com.fragdance.myflixclient.pages.utils.loadBitmapIntoImageView
import com.fragdance.myflixclient.pages.utils.loadDrawable
import com.fragdance.myflixclient.presenters.PersonRowPresenter
import com.fragdance.myflixclient.presenters.PosterPresenter
import com.fragdance.myflixclient.services.movieService
import com.fragdance.myflixclient.utils.StringsPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import utils.movieDetailsToVideo


class MovieDetailsPageOld :DetailsSupportFragment(){
    lateinit var mContext: Context
    lateinit var mView:ViewGroup
    var mDetails:IMovieDetails? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup

        if(mView != null) {
            var lp = mView!!.layoutParams as ViewGroup.MarginLayoutParams
            lp.leftMargin = 51
            mView!!.layoutParams = lp
        }

        return mView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()

        val args : MovieDetailsPageArgs by navArgs()
        val requestCall = movieService.getMovieDetails(args.id.toInt())
        requestCall.enqueue(object: Callback<IMovieDetails> {
            override fun onResponse(call: Call<IMovieDetails>, response: Response<IMovieDetails>) {
                if(response.isSuccessful) {
                    mDetails = response.body()!!
                    var rowsAdapter = ArrayObjectAdapter(createPresenterSelector(mDetails!!)).apply {
                        add(createDetailsOverviewRow(mDetails!!, this))
                    }

                    val castAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
                        for(cast in mDetails!!.cast) {
                            add(IPersonCardData(cast.person.id,cast.person.name,cast.character,cast.person.portrait))
                        }

                    }

                    var castRow = ListRow(HeaderItem(0, "Cast"), castAdapter)

                    rowsAdapter.add(castRow)

                    val crewAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
                        for(crew in mDetails!!.crew) {
                            add(IPersonCardData(crew.person.id,crew.person.name,crew.job,crew.person.portrait))
                        }
                    }
                    rowsAdapter.add(ListRow(HeaderItem(0, "Crew"), crewAdapter))
                    rowsAdapter.add(ListRow(HeaderItem(0, "Crew"), crewAdapter))
                    adapter = rowsAdapter

                    initializeBackground(mDetails)
                }else{
                    Toast.makeText(mContext, "Something went wrong ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<IMovieDetails>, t: Throwable) {
                Toast.makeText(mContext, "Something went wrong $t", Toast.LENGTH_LONG).show()
            }
        })
        onItemViewClickedListener = OnItemViewClickedListener{itemViewHolder,item,_,_->

            if(item is IPersonCardData) {

                findNavController().navigate(
                    MovieDetailsPageDirections.actionMovieDetailsFragmentToPersonDetails(item.id.toLong())
                )
            }
        }




    }

    private fun initializeBackground(movie: IMovieDetails?) {
        var backdropUrl = mDetails!!.backdrop
        if(backdropUrl != null && backdropUrl[0]=='/') {
            backdropUrl = Settings.SERVER+backdropUrl
        }
        /*
        var imageView = (mView as ViewGroup)?.findViewById<ImageView>(R.id.backdrop)
        if(imageView != null) {
            loadBitmapIntoImageView(
                mContext,
                backdropUrl,
                R.drawable.default_background,
                imageView
            )
        }

         */

        val backgroundController = DetailsSupportFragmentBackgroundController(this)
    backgroundController.solidColor = Color.rgb(18,18,18)
        backgroundController.enableParallax()
        loadBitmap(requireActivity(), backdropUrl, R.drawable.default_background) { bitmap ->
            backgroundController.coverBitmap = bitmap
            adapter.notifyItemRangeChanged(0, adapter.size())
        }


    }

    private fun createPresenterSelector(movie: IMovieDetails) = ClassPresenterSelector().apply {
        addClassPresenter(
            DetailsOverviewRow::class.java,
            createDetailsOverviewRowPresenter(movie, ::onActionClicked)
        )

        addClassPresenter(
            ListRow::class.java,
            PersonRowPresenter()
        )
    }

    private fun createDetailsOverviewRow(selectedVideo: IMovieDetails, detailsAdapter: ArrayObjectAdapter):
            DetailsOverviewRow {
        val context = requireContext()

        val row = DetailsOverviewRow(selectedVideo).apply {
            imageDrawable = ContextCompat.getDrawable(context, R.drawable.default_background)
            actionsAdapter = getActionAdapter()
        }

        val width = resources.getDimensionPixelSize(R.dimen.details_movie_poster_width)
        val height = resources.getDimensionPixelSize(R.dimen.details_movie_poster_height)

        var posterUrl = selectedVideo.poster;//selectedVideo.poster.substring(0,3)=="http"
        if(posterUrl[0]=='/') {
            posterUrl = Settings.SERVER+posterUrl
        }

        loadDrawable(requireActivity(), posterUrl, R.drawable.default_background, width,
            height)
        { resource ->
            row.imageDrawable = resource
            detailsAdapter.notifyArrayItemRangeChanged(0, detailsAdapter.size())
        }

        return row
    }

    private fun getActionAdapter() = ArrayObjectAdapter().apply {
        add(
            Action(
                ACTION_WATCH,
                resources.getString(R.string.watch_action_title)
            )
        )
        add(
            Action(
                ACTION_WATCH,
                resources.getString(R.string.watch_action_title)
            )
        )
        add(
            Action(
                ACTION_WATCH,
                resources.getString(R.string.watch_action_title)
            )
        )
        add(
            Action(
                ACTION_WATCH,
                resources.getString(R.string.watch_action_title)
            )
        )
        add(
            Action(
                ACTION_WATCH,
                resources.getString(R.string.watch_action_title)
            )
        )
    }


    class DetailsPresenter(presenter:Presenter,logoPresenter:PosterPresenter):FullWidthDetailsOverviewRowPresenter(presenter,logoPresenter) {
        /*
        override fun onLayoutLogo(viewHolder: ViewHolder?, oldState: Int, logoChanged: Boolean) {
            Timber.tag(Settings.TAG).d("onLayoutLogo")
            val resources = viewHolder?.view?.context?.resources;
            if(resources != null) {
                //super.onLayoutLogo(viewHolder, oldState, logoChanged)
                var v = viewHolder!!.logoViewHolder.view
                var lp = v.layoutParams as ViewGroup.MarginLayoutParams
                lp.topMargin = resources.getDimensionPixelSize(R.dimen.details_movie_poster_margin_top)
                lp.leftMargin = resources.getDimensionPixelSize(R.dimen.details_movie_poster_margin_left)
                v.layoutParams = lp
            }
        }
        */
    }


    private fun createDetailsOverviewRowPresenter(videoItem: IMovieDetails,
                                                  actionHandler: (Action, IMovieDetails) -> Unit): DetailsPresenter {
        return DetailsPresenter(DetailsDescriptionPresenter(), PosterPresenter()).apply {
            // Set detail background.
            backgroundColor = ContextCompat.getColor(mContext, R.color.selected_background)

            //isParticipatingEntranceTransition = true

            onActionClickedListener = OnActionClickedListener { actionHandler(it, videoItem) }
        }
    }

    private fun onActionClicked(action: Action, videoItem: IMovieDetails) {
        Timber.tag(Settings.TAG).d("onActionClicked");
        var playList = IPlayList()
        playList.videos.add(movieDetailsToVideo(videoItem))
        findNavController().navigate(
            MovieDetailsPageDirections.actionDetailsToPlayback(
                playList
            )
        )
        /*
        if (action.id == ACTION_WATCH) {
            val intent = VideoPlaybackActivity.newIntent(requireContext(), videoItem)
            startActivity(intent)
        }

         */
    }

    companion object {
        private const val ACTION_WATCH = 1L
    }
}