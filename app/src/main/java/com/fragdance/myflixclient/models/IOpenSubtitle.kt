package com.fragdance.myflixclient.models
import java.io.Serializable
data class IOpenSubtitle(
    val url:String,
    val langcode:String,
    val filename:String
):Serializable
