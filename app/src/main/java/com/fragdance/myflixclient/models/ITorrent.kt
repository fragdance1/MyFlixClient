package com.fragdance.myflixclient.models

import java.io.Serializable

data class ITorrent (
    val url:String,
    val hash:String,
    val quality:String,
    val resolution:String
): Serializable

data class IStatus (
    val status:String
    ):Serializable

data class ITorrentStatus (
    val status:String
):Serializable

data class IMovieTorrent(
        val id:String,
        val poster:String,
        val title:String,
    val torrents:List<ITorrent>

): Serializable

data class ITorrentFile (
    val name:String,
    val length:Long
        )

data class ITorrentDetails(
    val hash:String,
    val files:List<ITorrentFile>

)