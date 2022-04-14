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
        Timber.tag(Settings.TAG).d("Subtitle "+mVideo)
        val requestCall = subtitleService.searchSubtitle(mVideo.url,mVideo.title,null)
        requestCall.enqueue(object: Callback<List<ISubtitle>>{
            override fun onResponse(
                call: Call<List<ISubtitle>>,
                response: Response<List<ISubtitle>>
            ) {
                if(response.isSuccessful) {
                    val subtitles = response.body() as List<ISubtitle>
                    mAdapter.clear()
                    mAdapter.addAll(0,subtitles)
                    mVerticalGridView.requestFocus()
                }
            }

            override fun onFailure(call: Call<List<ISubtitle>>, t: Throwable) {

            }
        })
    }

    override fun onMenuItemClicked(item: Any) {
        val result = bundleOf("Item" to item)
        this.parentFragmentManager.setFragmentResult("Subtitle",result)
        parentFragmentManager.popBackStack()
    }

}

