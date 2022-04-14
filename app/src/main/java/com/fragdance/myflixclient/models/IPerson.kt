package com.fragdance.myflixclient.models
import java.io.Serializable
data class IPerson(val id:Int,val name:String,val portrait:String?):Serializable

data class IPersonCardData(val id:String,val title:String,val subtitle:String?,val portrait:String?):Serializable

data class IPersonDetails(
    val id:String,
    val name:String,
    val biography:String?,
    val portrait:String?,
    val cast:List<ICast>,
    val crew:List<ICrew>
): Serializable