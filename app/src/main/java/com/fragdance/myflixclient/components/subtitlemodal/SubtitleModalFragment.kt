package com.fragdance.myflixclient.components.subtitlemodal


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.VerticalGridView
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.menu.MenuItemBridgeAdapter
import com.fragdance.myflixclient.components.menu.MenuItemPresenter
import com.fragdance.myflixclient.models.ISubtitle
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.services.subtitleService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


interface OnMenuItemViewClickedListener {
    fun onMenuItemClicked(item:Any)
}

class SubtitleModalFragment(video: IVideo): Fragment(),OnMenuItemViewClickedListener {
    var mVideo = video
    lateinit var mVerticalGridView:VerticalGridView
    lateinit var mRootView:ViewGroup
    var mBridgeAdapter = MenuItemBridgeAdapter(this)

    var lrp = MenuItemPresenter()
    var mAdapter = ArrayObjectAdapter(lrp)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchSubtitle()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mRootView = inflater.inflate(R.layout.subtitle_modal, container, false) as ViewGroup
        mVerticalGridView = mRootView.findViewById(R.id.menu_view) as VerticalGridView
        mBridgeAdapter.setAdapter(mAdapter)

        mVerticalGridView.adapter = mBridgeAdapter

        return mRootView
    }

    private fun searchSubtitle() {
        val requestCall = subtitleService.searchSubtitle(mVideo.url,mVideo.title,mVideo.imdbId)
        requestCall.enqueue(object: Callback<List<ISubtitle>>{
            override fun onResponse(
                call: Call<List<ISubtitle>>,
                response: Response<List<ISubtitle>>
            ) {
                if(response.isSuccessful) {
                    Timber.tag(Settings.TAG).d("Subtitle search successful");
                    val subtitles = response.body() as List<ISubtitle>
                    Timber.tag(Settings.TAG).d("Got some subtitles "+subtitles.size)
                    mAdapter.clear()
                    if(subtitles.size > 0) {
                        mAdapter.addAll(0, subtitles)
                        mVerticalGridView.requestFocus()
                    } else {
                        // Add message 'No subtitles
                    }
                }
            }

            override fun onFailure(call: Call<List<ISubtitle>>, t: Throwable) {
                Timber.tag(Settings.TAG).d("Subtitle search failed");
            }
        })
    }

    override fun onMenuItemClicked(item: Any) {
        val result = bundleOf("Item" to item)
        this.parentFragmentManager.setFragmentResult("Subtitle",result)
        parentFragmentManager.popBackStack()
    }

}

