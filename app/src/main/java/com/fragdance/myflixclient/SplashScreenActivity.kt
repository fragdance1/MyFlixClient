package com.fragdance.myflixclient

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class SplashScreenActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        finish()
    }
}
