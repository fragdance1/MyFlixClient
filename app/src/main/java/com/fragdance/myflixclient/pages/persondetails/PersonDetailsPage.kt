package com.fragdance.myflixclient.pages.persondetails

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
import com.fragdance.myflixclient.components.action_bar.ActionBarButtonPresenter
import com.fragdance.myflixclient.components.action_bar.IPersonAction
import com.fragdance.myflixclient.components.moviecard.MovieCardPresenter
import com.fragdance.myflixclient.models.IPersonCardData
import com.fragdance.myflixclient.models.IPersonDetails
import com.fragdance.myflixclient.presenters.PersonRowPresenter
import com.fragdance.myflixclient.services.personService
import com.fragdance.myflixclient.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PersonDetailsPage: Fragment() {
    lateinit var mPerson:IPersonCardData
    lateinit var mContext: Context
    lateinit var mDetails: IPersonDetails
    private lateinit var mRootView:ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.person_details_view, container, false) as ViewGroup
        return mRootView
    }

    private fun createPresenterSelector(details: IPersonDetails) = ClassPresenterSelector().apply {
        addClassPresenter(
            IPersonDetails::class.java,
            PersonDetailsHeroPresenter()
        )

        addClassPresenter(
            ListRow::class.java,
            PersonRowPresenter()
        )
    }

    private fun setupView() {
        val content: VerticalGridView = mRootView.findViewById(R.id.content);

        if(mDetails is IPersonDetails) {
            // Add movie details

            val rowsAdapter = ArrayObjectAdapter(createPresenterSelector(mDetails!!))
            rowsAdapter.add(mDetails)

            if(mDetails?.cast?.size>0) {
                // Add row of cast members
                val castAdapter = ArrayObjectAdapter(MovieCardPresenter()).apply {
                    for (cast in mDetails!!.cast) {
                        if(cast.movie != null) {
                            add(castToMovieCard(cast))
                        } else {
                            Timber.tag(Settings.TAG).d("TODO: Implement cast/crew for tv-shows")
                        }
                    }
                }
                val castRow = ListRow(HeaderItem(0, "Cast"), castAdapter)
                rowsAdapter.add(castRow)
            }
            // Add row of crew members
            if(mDetails?.crew?.size > 0) {
                val crewAdapter = ArrayObjectAdapter(MovieCardPresenter()).apply {
                    for (crew in mDetails!!.crew) {
                        add(crewToMovieCard(crew))
                    }
                }
                rowsAdapter.add(ListRow(HeaderItem(0, "Crew"), crewAdapter))
            }
            val actionsAdapter = ArrayObjectAdapter(ActionBarButtonPresenter())
            var actionsRow = ListRow(actionsAdapter)
            actionsAdapter.add(IPersonAction("More", mDetails?.id));
            rowsAdapter.add(actionsRow)
            var bridgeAdapter = ItemBridgeAdapter()
            bridgeAdapter.setAdapter(rowsAdapter)
            content.adapter = bridgeAdapter
        }
    }

    override fun onViewCreated(view:View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
        //mPerson = PersonDetailsArg.fromBundle(requireArguments()).person
        val args :PersonDetailsPageArgs by navArgs()

        val requestCall = personService.getPersonDetails(args.id.toInt())

        requestCall.enqueue(object: Callback<IPersonDetails> {
            override fun onResponse(call: Call<IPersonDetails>, response: Response<IPersonDetails>) {
                if (response.isSuccessful) {
                    Timber.tag(Settings.TAG).d("Got response")
                    mDetails = response.body()!!
                    setupView()
                } else {
                    Timber.tag(Settings.TAG).d("Failed")
                }
            }

            override fun onFailure(call: Call<IPersonDetails>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }
}