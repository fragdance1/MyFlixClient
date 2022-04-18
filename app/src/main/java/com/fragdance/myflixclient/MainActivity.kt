package com.fragdance.myflixclient

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.leanback.widget.BrowseFrameLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.services.NetworkDiscoveryService
import com.fragdance.myflixclient.services.movieService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.fragdance.myflixclient.models.ITVShow
import com.fragdance.myflixclient.services.FayeService
import com.fragdance.myflixclient.services.sonarrService
import com.fragdance.myflixclient.utils.isEmulator
import org.cometd.bayeux.Message
import org.json.JSONObject
import timber.log.Timber


class MainView(context: Context, attrs: AttributeSet) : BrowseFrameLayout(context, attrs) {

}

class MainActivity : FragmentActivity() {
    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController
    private lateinit var mRootView: BrowseFrameLayout
    private lateinit var mBonjour: NetworkDiscoveryService
    private var mServerFoundReceiver: BroadcastReceiver? = null
    private var multicastLock: WifiManager.MulticastLock? = null

    // Load all the local movies into settings
    private fun loadMovies() {
        val requestCall = movieService.getLocalMovies()
        requestCall.enqueue(object : Callback<List<IMovie>> {
            override fun onResponse(call: Call<List<IMovie>>, response: Response<List<IMovie>>) {
                if (response.isSuccessful) {
                    Timber.tag(Settings.TAG).d("Movies loaded");
                    Settings.movies = response.body()!!
                    val intent = Intent()
                    intent.action = "movies_loaded"
                    intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                    sendBroadcast(intent)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Something went wrong ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Something went wrong $t", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun loadTVShows() {
        val requestCall = sonarrService.getSeries()
        requestCall.enqueue(object : Callback<List<ITVShow>> {
            override fun onResponse(call: Call<List<ITVShow>>, response: Response<List<ITVShow>>) {
                if (response.isSuccessful) {
                    Settings.tvshows = response.body()!!
                    val intent = Intent()
                    intent.action = "tvshows_loaded"
                    intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                    sendBroadcast(intent)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Something went wrong ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<ITVShow>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Something went wrong $t", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    fun test(message: Message?): Unit {

    }

    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            // Code here will run in UI thread
            val toast = Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            )
            val gravity = Gravity.RIGHT + Gravity.TOP
            toast.setGravity(gravity, 10, 10)

            val viewGroup = toast.view as ViewGroup?

            // Get the TextView of the toast
            val textView = viewGroup!!.getChildAt(0) as TextView

            // Set the text size
            //textView.textSize = 20f

            // Set the background color of toast
            viewGroup!!.setBackgroundColor(Color.parseColor("#10000000"))
            textView.setTextColor(Color.WHITE)
            toast.show()
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var service = FayeService()
        service.subscribe("/test/toaster") { message ->
            run {
                Timber.tag(Settings.TAG).d("Toast inside activity")
                showToast("Toast inside activity")
            }
        }
        service.subscribe("/torrent/download") { message: Message? ->
            run {
                if (message is Message) {
                    val obj = message?.dataAsMap
                    if (obj["msg"] == "finished") {
                        Timber.tag(Settings.TAG).d("Finished downloading")
                        this.showToast("Finished downloading " + obj["folder"])
                    }

                    // Timber.tag(Settings.TAG).d("Torrent download " + obj)
                }
            }
        }
        MainActivity.mInstance = this;
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val displayMetrics = this.resources.displayMetrics
        Settings.WIDTH = displayMetrics.widthPixels.toFloat()
        Settings.HEIGHT = displayMetrics.heightPixels.toFloat()

        if (isEmulator()) { // Bonjour doesn't work on emulator
            Timber.tag(Settings.TAG).d("Running on emulator");
            Settings.SERVER = "http://192.168.1.80:5001"
            loadStartingPage()
        } else {
            mServerFoundReceiver = object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    Timber.tag(Settings.TAG).d("Server received " + Settings.SERVER)
                    loadStartingPage()
                }
            }
            val filter = IntentFilter()
            filter.addAction("server_found")
            applicationContext.registerReceiver(mServerFoundReceiver, filter)
            mBonjour = NetworkDiscoveryService(applicationContext)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        /*
        if(multicastLock is WifiManager.MulticastLock) {
            multicastLock?.release()
        }

         */
        if (mServerFoundReceiver != null) {
            applicationContext.unregisterReceiver(mServerFoundReceiver)
            mServerFoundReceiver = null
        }
    }

    private fun loadStartingPage() {
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        mRootView = findViewById(R.id.main_browse_fragment)
        mRootView.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { focused, direction ->
                if (direction == View.FOCUS_LEFT) {
                    findViewById(R.id.side_menu)
                } else {
                    null
                }
            }
        navGraph.startDestination = R.id.homePage
        loadMovies()
        loadTVShows()
        navController.graph = navGraph
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent == null) {
            return
        }
    }


    companion object {
        lateinit var mInstance: MainActivity
        fun showToast(message: String): Unit {
            mInstance.showToast(message)
        }

    }

}