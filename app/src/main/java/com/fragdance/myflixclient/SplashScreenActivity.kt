package com.fragdance.myflixclient

import android.content.Intent

import androidx.core.content.ContextCompat.startActivity

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import timber.log.Timber

class SplashScreenActivity : FragmentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.splash)
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }
    }
