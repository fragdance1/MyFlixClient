package com.fragdance.myflixclient.pages.videoplayer

import android.content.Context
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter

class ProgressTransportControlGlue<T: PlayerAdapter> (context: Context, impl:T, private val updateProgress:()->Unit):PlaybackTransportControlGlue<T>(context,impl){

}