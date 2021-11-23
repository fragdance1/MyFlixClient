package com.fragdance.myflixclient.models

import com.google.android.exoplayer2.source.TrackGroupArray

data class ITrackMenuItem(
    val label: String,
    val trackId: Int,
    val groupIndex: Int?,
    val renderIndex: Int?,
    var trackGroups: TrackGroupArray?
)
