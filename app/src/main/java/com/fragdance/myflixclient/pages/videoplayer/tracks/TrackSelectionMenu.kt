package com.fragdance.myflixclient.pages.videoplayer.tracks

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.fragdance.myflixclient.models.ITrackMenuItem
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider


class TrackSelectionMenu : VerticalGridSupportFragment(), OnItemViewClickedListener {
    private var menuItems: ArrayObjectAdapter? = null
    private var subtitles: ArrayList<ITrackMenuItem> = arrayListOf()
    private var audiotracks: ArrayList<ITrackMenuItem> = arrayListOf()
    private var mTrackSelector: TrackSelector? = null

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

    }

    // Set the selectable tracks for this video (subtitles, audiotracks)
    @SuppressLint("NotifyDataSetChanged")
    fun setTracks(trackSelector: DefaultTrackSelector) {
        mTrackSelector = trackSelector
        var audioRenderIndex = 0
        var subtitleRenderIndex = 0
        val mappedTracksInfo = trackSelector.currentMappedTrackInfo
        subtitles.clear()
        audiotracks.clear()
        if (mappedTracksInfo == null) return

        val parameters = trackSelector.parameters
        for (rendererIndex in 0 until mappedTracksInfo.rendererCount) {
            if (parameters.getRendererDisabled(rendererIndex))
                continue


            val trackType = mappedTracksInfo.getRendererType(rendererIndex)
            val trackGroupArray = mappedTracksInfo.getTrackGroups(rendererIndex)

            if (trackType == C.TRACK_TYPE_TEXT) {
                subtitleRenderIndex = rendererIndex
            } else if (trackType == C.TRACK_TYPE_AUDIO) {
                audioRenderIndex = rendererIndex
            }
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

            val items: ArrayList<ITrackMenuItem> = arrayListOf()
            if (subtitles.size > 0) {
                items.add(ITrackMenuItem("Subtitles", -1, null, null, null))
                items.add(ITrackMenuItem("Off", -2, null, subtitleRenderIndex, null))
                items.addAll(subtitles)
                items.add(ITrackMenuItem("Download", -3, null, subtitleRenderIndex, null))
            }
            if (audiotracks.size > 0) {
                items.add(ITrackMenuItem("Audio", -1, null, null, null))
                items.add(ITrackMenuItem("Off", -2, null, audioRenderIndex, null))
                items.addAll(audiotracks)
            }

            menuItems?.clear()
            menuItems?.addAll(0, items)

            adapter = menuItems
            with(gridPresenter as TrackMenuPresenter) {
                gridView?.adapter?.notifyDataSetChanged()
            }
        }
    }


    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

        val menuItem = item as ITrackMenuItem;
        if (menuItem.trackId >= 0) {
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

}

