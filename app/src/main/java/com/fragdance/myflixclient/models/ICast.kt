package com.fragdance.myflixclient.models
import java.io.Serializable
data class ICast(val person:IPerson,val character:String,val movie:IMovie?,val show:ITVShow):Serializable
