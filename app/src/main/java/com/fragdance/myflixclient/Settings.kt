package com.fragdance.myflixclient

import com.fragdance.myflixclient.models.IMovie

class Settings {
    companion object {
        const val SERVER:String = "http://192.168.1.80:5001"// "http://192.168.2.121:5001";//"http://10.10.10.233:5001"
        const val TAG:String = "MyFlix"

        var WIDTH:Float = 0.0f
        var HEIGHT:Float = 0.0f

        /* All the local movies */
        var movies:List<IMovie> = listOf()
    }
}