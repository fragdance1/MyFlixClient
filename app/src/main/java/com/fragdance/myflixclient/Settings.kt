package com.fragdance.myflixclient

import com.fragdance.myflixclient.models.IMovie

class Settings {
    companion object {
        const val SERVER:String = "https://myflix.eu.ngrok.io";// "http://192.168.2.121:5001";//"http://192.168.2.121:5001"
        const val TAG:String = "MyFlix"
        var movies:List<IMovie> = listOf()
    }
}