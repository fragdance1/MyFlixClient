package com.fragdance.myflixclient.pages.persondetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.peroncard.PersonCardPresenter
import com.fragdance.myflixclient.models.IPersonCardData
import com.fragdance.myflixclient.models.IPersonDetails
import com.fragdance.myflixclient.pages.moviedetails.DetailsDescriptionPresenter
import com.fragdance.myflixclient.pages.moviedetails.MovieDetailsPage
import com.fragdance.myflixclient.pages.moviedetails.MovieDetailsPageDirections
import com.fragdance.myflixclient.pages.utils.loadDrawable
import com.fragdance.myflixclient.presenters.PersonRowPresenter
import com.fragdance.myflixclient.presenters.PosterPresenter
import com.fragdance.myflixclient.services.personService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PersonDetailsPage: DetailsSupportFragment() {
    lateinit var mPerson:IPersonCardData
    lateinit var mContext: Context
    lateinit var mDetails: IPersonDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        //mPerson = PersonDetailsArg.fromBundle(requireArguments()).person
        val args :PersonDetailsPageArgs by navArgs()


        val requestCall = personService.getPersonDetails(args.id.toInt())
        requestCall.enqueue(object: Callback<IPersonDetails> {
            override fun onResponse(call: Call<IPersonDetails>, response: Response<IPersonDetails>) {
                if (response.isSuccessful) {
                    mDetails = response.body()!!

                    title = mDetails.name
                    var rowsAdapter = ArrayObjectAdapter(createPresenterSelector(mDetails!!)).apply {
                        add(createDetailsOverviewRow(mDetails!!, this))
                    }
                    if(mDetails!!.cast.isNotEmpty()) {
                        val castAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
                            for (cast in mDetails!!.cast) {
                                add(
                                    IPersonCardData(
                                        cast.movie!!.id,
                                        cast.movie!!.title,
                                        cast.character,
                                        cast.movie!!.poster
                                    )
                                )
                            }

                        }
                        var castRow = ListRow(HeaderItem(0, "Cast"), castAdapter)

                        rowsAdapter.add(castRow)
                    }
                    if(mDetails!!.crew.isNotEmpty()) {
                        val crewAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
                            for (crew in mDetails!!.crew) {
                                add(
                                    IPersonCardData(
                                        crew.movie!!.id,
                                        crew.movie!!.title,
                                        crew.job,
                                        crew.movie!!.poster
                                    )
                                )
                            }

                        }
                        var castRow = ListRow(HeaderItem(0, "Crew"), crewAdapter)

                        rowsAdapter.add(castRow)
                    }

                    adapter = rowsAdapter
                } else {

                }
            }

            override fun onFailure(call: Call<IPersonDetails>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
        onItemViewClickedListener = OnItemViewClickedListener{itemViewHolder,item,_,_->

            if(item is IPersonCardData) {

                findNavController().navigate(
                    PersonDetailsPageDirections.actionPersonDetailsToMovieDetailsFragment(item.id.toLong())
                )
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)

        return view
    }

    private fun createPresenterSelector(details: IPersonDetails) = ClassPresenterSelector().apply {
        addClassPresenter(
            DetailsOverviewRow::class.java,
            createDetailsOverviewRowPresenter(details)
        )

        addClassPresenter(
            ListRow::class.java,
            PersonRowPresenter()
        )


    }

    private fun createDetailsOverviewRow(details: IPersonDetails, detailsAdapter: ArrayObjectAdapter):
            DetailsOverviewRow {
        val context = requireContext()

        val row = DetailsOverviewRow(details).apply {
            imageDrawable = ContextCompat.getDrawable(context, R.drawable.default_background)

        }

        val width = resources.getDimensionPixelSize(R.dimen.details_movie_poster_width)
        val height = resources.getDimensionPixelSize(R.dimen.details_movie_poster_height)

        var posterUrl = details.portrait;//selectedVideo.poster.substring(0,3)=="http"
        if(posterUrl!= null && posterUrl[0]=='/') {
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

    private fun createDetailsOverviewRowPresenter(details: IPersonDetails): PersonDetailsPage.DetailsPresenter {
        return PersonDetailsPage.DetailsPresenter(
            DetailsDescriptionPresenter(),
            PosterPresenter()
        ).apply {
            // Set detail background.
            backgroundColor =
                ContextCompat.getColor(mContext, R.color.selected_background)

            isParticipatingEntranceTransition = true

            //onActionClickedListener = OnActionClickedListener { actionHandler(it, details) }
        }
    }
    class DetailsPresenter(presenter:Presenter,logoPresenter: PosterPresenter):FullWidthDetailsOverviewRowPresenter(presenter,logoPresenter) {
        override fun onLayoutLogo(viewHolder: ViewHolder?, oldState: Int, logoChanged: Boolean) {

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
    }

}