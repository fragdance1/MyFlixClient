package com.fragdance.myflixclient.models

import com.google.android.exoplayer2.text.Subtitle
import java.io.Serializable

data class ILanguage(
    val id:Int,
    val code:String,
    val english:String,
    val swedish:String
):Serializable

data class ISubtitle(
    val id:Int, // Can be -1 for not downloaded yet
    val url:String?,
    val language:ILanguage?,
    val filename:String,
    val open_subtitle_id:String?,
    var srt:String?,
    var subtitle: Subtitle?
):Serializable
