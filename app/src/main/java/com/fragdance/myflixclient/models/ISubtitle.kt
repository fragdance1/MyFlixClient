package com.fragdance.myflixclient.models

import com.google.android.exoplayer2.text.Subtitle
import java.io.Serializable

data class ISubtitle(
    val id:Int, // Can be -1 for not downloaded yet
    val url:String,
    val language:String = "und",
    val filename:String,
    var srt:String?,
    var subtitle: Subtitle?
):Serializable
