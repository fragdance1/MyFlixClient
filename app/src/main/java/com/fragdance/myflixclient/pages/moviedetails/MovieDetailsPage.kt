package com.fragdance.myflixclient.pages.moviedetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.navArgs
import com.fragdance.myflixclient.MainActivity
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.MyFlixListRow
import com.fragdance.myflixclient.components.personcard.PersonCardPresenter
import com.fragdance.myflixclient.models.ICast
import com.fragdance.myflixclient.models.ICrew
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IPersonCardData
import com.fragdance.myflixclient.presenters.MovieDetailsHeroPresenter
import com.fragdance.myflixclient.presenters.MyFlixListRowPresenter

import com.fragdance.myflixclient.presenters.PersonRowPresenter
import com.fragdance.myflixclient.services.movieService
import com.fragdance.myflixclient.utils.castToPersonCard
import com.fragdance.myflixclient.utils.crewToPersonCard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class MovieDetailsPage : Fragment() {
    lateinit var mContext: Context
    lateinit var mRootView: ViewGroup
    var mDetails: IMovieDetails? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.movie_details_view, container, false) as ViewGroup

        return mRootView
    }

    private fun createPresenterSelector(movie: IMovieDetails) = ClassPresenterSelector().apply {
        addClassPresenter(
            ListRow::class.java,
            MyFlixListRowPresenter()
        )
        addClassPresenter(
            IPersonCardData::class.java,
            PersonRowPresenter()
        )
        addClassPresenter(
            IMovieDetails::class.java,
            MovieDetailsHeroPresenter()
        )
    }

    private fun setupView() {
        val content: VerticalGridView = mRootView.findViewById(R.id.content);
        if(mDetails is IMovieDetails) {
            // Add movie details
            val rowsAdapter = ArrayObjectAdapter(createPresenterSelector(mDetails!!))
            rowsAdapter.add(mDetails)

            // Add row of cast members
            if(mDetails!!.cast != null) {
                val castAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
                    for (cast in mDetails!!.cast) {
                        add(castToPersonCard(cast))
                    }
                }
                val castRow = MyFlixListRow(HeaderItem(0, "Cast"), castAdapter)
                rowsAdapter.add(castRow)
            }
            // Add row of crew members
            val crewAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
                if(mDetails!!.crew != null) {
                    for (crew in mDetails!!.crew) {
                        add(crewToPersonCard(crew))
                    }
                }
            }
            rowsAdapter.add(ListRow(HeaderItem(0, "Crew"), crewAdapter))

            var bridgeAdapter = ItemBridgeAdapter()
            bridgeAdapter.setAdapter(rowsAdapter)
            content.adapter = bridgeAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()

        val args: MovieDetailsPageArgs by navArgs()
        val getMovieDetailsCall = movieService.getMovieDetails(args.id)

        if (mDetails == null) {
            getMovieDetailsCall.enqueue(object : Callback<IMovieDetails> {
                override fun onResponse(
                    call: Call<IMovieDetails>,
                    response: Response<IMovieDetails>
                ) {
                    if (response.isSuccessful) {
                        mDetails = response.body()!!
                        setupView()
                    }
                }

                override fun onFailure(call: Call<IMovieDetails>, t: Throwable) {
                    MainActivity.showToast("Error: Failed to download movie details")
                }
            })

        } else {
            setupView()
        }
    }
}


