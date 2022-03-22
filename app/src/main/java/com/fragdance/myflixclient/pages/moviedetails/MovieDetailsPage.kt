package com.fragdance.myflixclient.pages.moviedetails

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.peroncard.PersonCardPresenter
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IPersonCardData
import com.fragdance.myflixclient.models.IPlayList
import com.fragdance.myflixclient.pages.utils.loadBitmap
import com.fragdance.myflixclient.pages.utils.loadDrawable
import com.fragdance.myflixclient.presenters.PersonRowPresenter
import com.fragdance.myflixclient.presenters.PosterPresenter
import com.fragdance.myflixclient.services.movieService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import utils.movieDetailsToVideo
import androidx.leanback.widget.ItemBridgeAdapter
import com.fragdance.myflixclient.presenters.MovieDetailsHeroPresenter


class MovieDetailsPage: Fragment() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


/*
        hero.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Settings.HEIGHT.toInt())
*/

//        var persons:VerticalGridView = view.findViewById(R.id.persons);
//        var rowsAdapter = ArrayObjectAdapter(createPresenterSelector(mDetails!!))
    /*.apply {
            add(createDetailsOverviewRow(mDetails!!, this))
        }*/
        /*
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

         */
        //var bridgeAdapter = ItemBridgeAdapter();
        //bridgeAdapter.setAdapter(rowsAdapter)
        //persons.adapter = bridgeAdapter
/*
        var cast:View = view.findViewById(R.id.cast)

        cast.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            Settings.HEIGHT.toInt())

        var crew:View = view.findViewById(R.id.crew)

        crew.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            Settings.HEIGHT.toInt())
*/
        /*
        var scrollView: ScrollView = view.findViewById(R.id.movie_details_view)
        scrollView.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            Timber.tag(Settings.TAG).d("On scroll");
        }

         */
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
    fun bringToBack(v:View) {
        var parent:ViewGroup = v.parent as ViewGroup
        var index:Int = parent.indexOfChild(v);
        for(i in 0..index) {
            parent.bringChildToFront(parent.getChildAt(i))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()

        //
        val args : MovieDetailsPageArgs by navArgs()
        val requestCall = movieService.getMovieDetails(args.id.toInt())

        requestCall.enqueue(object: Callback<IMovieDetails> {
            override fun onResponse(call: Call<IMovieDetails>, response: Response<IMovieDetails>) {
                if (response.isSuccessful) {
                    /*


                    var backdrop: ImageView = mRootView.findViewById(R.id.backdrop)
                    Picasso.get()
                        .load(Settings.SERVER+mDetails!!.backdrop)
                        .into(backdrop)
*/
                    mDetails = response.body()!!

                var persons:VerticalGridView = mRootView.findViewById(R.id.persons);

                var rowsAdapter = ArrayObjectAdapter(createPresenterSelector(mDetails!!))
                    rowsAdapter.add(mDetails)
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

                var bridgeAdapter = ItemBridgeAdapter();
                bridgeAdapter.setAdapter(rowsAdapter)
                persons.adapter = bridgeAdapter

                    persons.bringToFront()
/*
                val cast:HorizontalGridView = mRootView.findViewById(R.id.cast);
                val castAdapter = ItemBridgeAdapter()
                val castRowAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
                    for(cast in mDetails!!.cast) {
                        add(IPersonCardData(cast.person.id,cast.person.name,cast.character,cast.person.portrait))
                    }

                }
                castAdapter.setAdapter(castRowAdapter)
                cast.adapter = castAdapter

                val crew:HorizontalGridView = mRootView.findViewById(R.id.crew);
                val crewAdapter = ItemBridgeAdapter()
                val crewRowAdapter = ArrayObjectAdapter(PersonCardPresenter()).apply {
                    for(crew in mDetails!!.crew) {
                        add(IPersonCardData(crew.person.id,crew.person.name,crew.job,crew.person.portrait))
                    }

                }
                crewAdapter.setAdapter(crewRowAdapter)
                crew.adapter = crewAdapter

 */}
            }
            override fun onFailure(call: Call<IMovieDetails>, t: Throwable) {
                Toast.makeText(mContext, "Something went wrong $t", Toast.LENGTH_LONG).show()
            }
        })
    }
}


