package com.fragdance.myflixclient.models

import java.io.Serializable

data class IVideoFile(
   var id:Int,
   var name:String,
   var extension:String,
   var folder:String,
   var size:Long,
   var runtime:Int,
   var coded:String,
   var width:Int,
   var height:Int,
   /*
   var audio_tracks:IAudioTrack[],
   var subtitle_tracks:ISubtitleTrack[],


    */
   var subtitles:List<ISubtitle> = emptyList(),
   var movie:IMovie?

):Serializable

