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
   var subtitles:List<ISubtitle> = emptyList(),
   var movie:IMovie?

):Serializable

data class IDisc(
   var id:Int,
   var type:String,
   var upc:String
)