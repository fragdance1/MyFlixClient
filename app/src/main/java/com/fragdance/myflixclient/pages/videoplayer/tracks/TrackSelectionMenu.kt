package com.fragdance.myflixclient.pages.videoplayer.tracks

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentResultListener
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.subtitlemodal.SubtitleModalFragment
import com.fragdance.myflixclient.models.IOpenSubtitle
import com.fragdance.myflixclient.models.ISubtitle
import com.fragdance.myflixclient.models.ITrackMenuItem
import com.fragdance.myflixclient.models.IVideo
import com.fragdance.myflixclient.pages.videoplayer.VideoPlayerFragment
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider
import com.google.android.exoplayer2.util.MimeTypes
import com.google.common.collect.ImmutableSet
import timber.log.Timber


class TrackSelectionMenu : VerticalGridSupportFragment(), OnItemViewClickedListener {
    private var menuItems: ArrayObjectAdapter? = null
    private var subtitles: ArrayList<ITrackMenuItem> = arrayListOf()
    private var audiotracks: ArrayList<ITrackMenuItem> = arrayListOf()
    private var mTrackSelector: TrackSelector? = null
    private var mPlayerFragment:VideoPlayerFragment? = null
    private var mVideo:IVideo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val presenter = TrackMenuPresenter()
        presenter.enableChildRoundedCorners(false)
        presenter.shadowEnabled = false

        presenter.numberOfColumns = 1

        gridPresenter = presenter
        menuItems = ArrayObjectAdapter(TrackMenuItemSelector())

        adapter = menuItems
        onItemViewClickedListener = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayMetrics = requireContext().resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.3).toInt()
        val height = displayMetrics.heightPixels
        val lp = FrameLayout.LayoutParams(width, height)
        lp.gravity = Gravity.END

        view.layoutParams = lp
        view.setBackgroundColor(Color.argb(0.8f, 0f, 0f, 0f))
        Timber.tag(Settings.TAG).d("TrackSelectionMenu.onViewCreated")
    }

    private fun getEmbeddedTracks(trackSelector:DefaultTrackSelector) {

        val mappedTracksInfo = trackSelector.currentMappedTrackInfo ?: return

        val parameters = trackSelector.parameters
        for (rendererIndex in 0 until mappedTracksInfo.rendererCount) {
            if (parameters.getRendererDisabled(rendererIndex))
                continue
            val trackType = mappedTracksInfo.getRendererType(rendererIndex)
            val trackGroupArray = mappedTracksInfo.getTrackGroups(rendererIndex)


            for (groupIndex in 0 until trackGroupArray.length) {
                for (trackIndex in 0 until trackGroupArray[groupIndex].length) {
                    val isTrackSupported = mappedTracksInfo.getTrackSupport(
                        rendererIndex,
                        groupIndex,
                        trackIndex
                    ) == C.FORMAT_HANDLED

                    if (!isTrackSupported) continue

                    var label = trackGroupArray[groupIndex].getFormat(trackIndex).label
                    if (label == null) {
                        label = DefaultTrackNameProvider(resources).getTrackName(
                            trackGroupArray[groupIndex].getFormat(trackIndex)
                        )
                    }


                    val item = ITrackMenuItem(
                        label,
                        trackIndex,
                        groupIndex,
                        rendererIndex,
                        trackGroupArray
                    )

                    if (trackType == C.TRACK_TYPE_TEXT) {
                        subtitles.add(item)
                    } else if (trackType == C.TRACK_TYPE_AUDIO) {
                        audiotracks.add(item)
                    }

                }
            }
        }
    }
    // Set the selectable tracks for this video (subtitles, audiotracks)

    @SuppressLint("NotifyDataSetChanged")
    fun setup(trackSelector: DefaultTrackSelector?,video: IVideo,playerFragment:VideoPlayerFragment) {
        //mTrackSelector = trackSelector
        mVideo = video
        mPlayerFragment = playerFragment

        subtitles.clear()
        audiotracks.clear()
        mTrackSelector = trackSelector
        if(trackSelector != null) {
            getEmbeddedTracks(trackSelector)
        }

        for((index, sub) in mPlayerFragment!!.mExternalSubtitles.withIndex()) {
            subtitles.add(ITrackMenuItem(sub.language, EXTERNAL_SUBTITLE,index,null,null))
        }
        val items: ArrayList<ITrackMenuItem> = arrayListOf()
            items.add(ITrackMenuItem("Subtitles", HEADER, null, null, null))
            items.add(ITrackMenuItem("Off", DISABLE_SUBTITLE, null, 0, null))
            items.addAll(subtitles)
            items.add(ITrackMenuItem("Download", DOWNLOAD_SUBTITLE, null, 0, null))

        if (audiotracks.size > 0) {
            items.add(ITrackMenuItem("Audio", HEADER, null, null, null))
            items.add(ITrackMenuItem("Off", DISABLE_AUDIO, null, 0, null))
            items.addAll(audiotracks)
        }

        menuItems?.clear()
        menuItems?.addAll(0, items)

        adapter = menuItems
        with(gridPresenter as TrackMenuPresenter) {
            gridView?.adapter?.notifyDataSetChanged()
        }

    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

        parentFragmentManager.popBackStack()
        parentFragmentManager.beginTransaction().hide(this).commit()
        val menuItem = item as ITrackMenuItem;
        if(menuItem.trackId == EXTERNAL_SUBTITLE) {
            Timber.tag(Settings.TAG).d("Selecting external subtitle")
            mPlayerFragment!!.selectExternalSubtitle(menuItem.groupIndex!!);
        } else if(menuItem.trackId == DOWNLOAD_SUBTITLE) {
            Timber.tag(Settings.TAG).d("Download subtitle");
            var modal = SubtitleModalFragment(mVideo!!)

            parentFragmentManager
                .beginTransaction()
                .replace(R.id.subtitle_search_dock,modal)
                .addToBackStack("subtitle").commit()

            parentFragmentManager.setFragmentResultListener("Subtitle",modal, FragmentResultListener{key,result->
                Timber.tag(Settings.TAG).d("Fragment listener "+result);
                var receivedData = result.get("Item") as ISubtitle
                if(receivedData!=null) {
                    Timber.tag(Settings.TAG).d(receivedData.toString())
                    mPlayerFragment?.downloadSubtitle(receivedData!!)
                }
                parentFragmentManager.beginTransaction().remove(modal).commit()

            })
        } else if(menuItem.trackId == DISABLE_SUBTITLE) {
            Timber.tag(Settings.TAG).d("Disable subtitle");
            mPlayerFragment!!.disableSubtitles()
        } else if(menuItem.trackId == DISABLE_AUDIO) {
            Timber.tag(Settings.TAG).d("Disable audio")
        } else {
            Timber.tag(Settings.TAG).d("Selecting internal track")
            val override =
                DefaultTrackSelector.SelectionOverride(menuItem.groupIndex!!, menuItem.trackId)
            val parametersBuilder = DefaultTrackSelector.ParametersBuilder(requireContext())
            parametersBuilder.setSelectionOverride(
                menuItem.renderIndex!!,
                menuItem.trackGroups!!,
                override
            )
            mTrackSelector?.parameters = parametersBuilder.build()
        }

    }

    companion object {
        val HEADER = -1
        val DOWNLOAD_SUBTITLE = -2
        val EXTERNAL_SUBTITLE = -3
        val DISABLE_SUBTITLE = -4
        val DISABLE_AUDIO = -5
    }
}

