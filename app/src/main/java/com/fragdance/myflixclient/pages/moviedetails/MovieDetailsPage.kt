package com.fragdance.myflixclient.pages.moviedetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.navArgs
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.personcard.PersonCardPresenter
import com.fragdance.myflixclient.services.movieService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import androidx.leanback.widget.ItemBridgeAdapter
import com.fragdance.myflixclient.components.subtitlemodal.OnMenuItemViewClickedListener
import com.fragdance.myflixclient.models.*
import com.fragdance.myflixclient.presenters.*


class MovieDetailsPage : Fragment(), OnMenuItemViewClickedListener {
    lateinit var mContext: Context
    lateinit var mRootView: ViewGroup
    var mDetails: IMovieDetails? = null
    var mThis = this;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //super.onCreateView(inflater,container,savedInstanceState)
        Timber.tag(Settings.TAG).d("MovieDetailsPage.onCreate");
        mRootView = inflater.inflate(R.layout.movie_details_view, container, false) as ViewGroup
        return mRootView
    }


    private fun createPresenterSelector(movie: IMovieDetails) = ClassPresenterSelector().apply {
        addClassPresenter(
            ListRow::class.java,
            PersonRowPresenter()
        )
        addClassPresenter(
            IMovieDetails::class.java,
            MovieDetailsHeroPresenter()
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Timber.tag(Settings.TAG).d("onSaveInstanceState");
        super.onSaveInstanceState(outState)
    }

    /*
        public void onSaveInstanceState(Bundle outState) {

            outState.putBoolean("restore", true);
            outState.putInt("nAndroids", 2);
            super.onSaveInstanceState(outState);
        }
        */
    fun setupView() {

        var persons: VerticalGridView = mRootView.findViewById(R.id.persons);

        var rowsAdapter = ArrayObjectAdapter(createPresenterSelector(mDetails!!))
        rowsAdapter.add(mDetails)

        val castAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
            for (cast in mDetails!!.cast) {
                add(
                    IPersonCardData(
                        cast.person.id.toString(),
                        cast.person.name,
                        cast.character,
                        cast.person.portrait
                    )
                )
            }
        }
        var castRow = ListRow(HeaderItem(0, "Cast"), castAdapter)

        rowsAdapter.add(castRow)

        val crewAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
            for (crew in mDetails!!.crew) {
                add(
                    IPersonCardData(
                        crew.person.id.toString(),
                        crew.person.name,
                        crew.job,
                        crew.person.portrait
                    )
                )
            }
        }
        rowsAdapter.add(ListRow(HeaderItem(0, "Crew"), crewAdapter))

        var bridgeAdapter = ItemBridgeAdapter()//MenuItemBridgeAdapter(mThis);
        bridgeAdapter.setAdapter(rowsAdapter)
        persons.adapter = bridgeAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        mContext = requireContext()

        //
        val args: MovieDetailsPageArgs by navArgs()
        val getMovieDetails = movieService.getMovieDetails(args.id)


        val mThis = this;
        if (mDetails == null) {
            getMovieDetails.enqueue(object : Callback<IMovieDetails> {
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
                    Toast.makeText(mContext, "Something went wrong $t", Toast.LENGTH_LONG).show()
                }
            })

        } else {
            setupView()
        }
    }

    override fun onMenuItemClicked(item: Any) {
        Timber.tag(Settings.TAG).d("onMenuItemClicked " + item);
    }


}


