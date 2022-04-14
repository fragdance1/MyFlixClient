package com.fragdance.myflixclient

import android.app.Application
import android.os.StrictMode
import com.fragdance.myflixclient.services.FayeService
import timber.log.Timber

class MyFlixApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        //FayeService.get()
        if (BuildConfig.DEBUG) {
            enableStrictMode()
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }

}


