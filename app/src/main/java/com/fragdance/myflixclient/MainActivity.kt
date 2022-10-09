package com.fragdance.myflixclient

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.leanback.widget.BrowseFrameLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.ITVShow
import com.fragdance.myflixclient.utils.isEmulator
import org.cometd.bayeux.Message
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import android.Manifest
import android.animation.ObjectAnimator
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.fragdance.myflixclient.services.*
import org.json.JSONObject
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import com.fragdance.myflixclient.utils.MovieLoaders
import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

class MainView(context: Context, attrs: AttributeSet) : BrowseFrameLayout(context, attrs) {

}

class MainActivity : FragmentActivity() {
    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController
    private lateinit var mRootView: BrowseFrameLayout
    private lateinit var mBonjour: NetworkDiscoveryService
    private var mServerFoundReceiver: BroadcastReceiver? = null
    var contentHasLoaded = false

   // Load all the local movies into settings
    private fun loadMovies() {
       runOnUiThread { findViewById<TextView>(R.id.splashMessage).text = "Loading movies" }

        try {
            var response = movieService.getLocalMovies().execute()
            if (response.isSuccessful) {
                var editor = getSharedPreferences("myflix", MODE_PRIVATE).edit();
                editor.putString("server", Settings.SERVER)
                editor.commit()
                Settings.movies = response.body()!!
                val intent = Intent()
                intent.action = "movies_loaded"
                intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                sendBroadcast(intent)
                //loadTVShows()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Something went wrong ${response.message()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch(e:Exception) {
            Timber.tag(Settings.TAG).d("Failed to load movies")
        }


    }

    // Load all the local tv shows into settings
    private fun loadTVShows() {
        runOnUiThread { findViewById<TextView>(R.id.splashMessage).text = "Loading TV-shows"}
        try {
            var response = tvShowService.getShows().execute()
            if (response.isSuccessful) {
                Settings.tvshows = response.body()!!
                val intent = Intent()
                intent.action = "tvshows_loaded"
                intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                sendBroadcast(intent)
                //setupView()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "loadTVShows Something went wrong ${response.message()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch(e:Exception) {

        }
    }

    private fun loadCategories() {

    }
    // Show a toast
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

    private fun setupView() {
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.loading_progress).visibility = View.INVISIBLE

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        navController.graph = navGraph

        loadStartingPage();
    }

    private fun pingServer(server:String) : Boolean {
        try {
            Settings.SERVER = server;
            serverService.ping().execute()
            return true
        }catch( e:Exception) {
            Settings.SERVER = ""
            return false;
        }
    }

    fun init()  {
        GlobalScope.launch {
            getServer()
            //FayeService.create()
            //loadMovies()
            //checkRunTimePermission()
        }

    }
    private fun loadHomePageMovies(type:String) {
        runOnUiThread { findViewById<TextView>(R.id.splashMessage).text = "Loading "+type }
        MovieLoaders.reloadMovies(type,null)
    }
    private fun startup() {
        Timber.tag(Settings.TAG).d("startup")
        FayeService.create()
        Timber.tag(Settings.TAG).d("loadMovies")
        loadMovies()
        Timber.tag(Settings.TAG).d("loadTVShows")
        loadTVShows()
        Timber.tag(Settings.TAG).d("checkRuntimePermissions")
        checkRunTimePermission()
        Timber.tag(Settings.TAG).d("loadGenre")
        Timber.tag(Settings.TAG).d("setupView")
        loadHomePageMovies("Latest")
        loadHomePageMovies("Recommended")
        loadHomePageMovies("Boxoffice")
        loadHomePageMovies("In Progress")
        for(genre in Settings.MOVIE_GENRES) {
            loadHomePageMovies(genre)
        }
        runOnUiThread { setupView()}
    }
    // Try to retrieve the server address
    private fun getServer() {
        runOnUiThread { findViewById<TextView>(R.id.splashMessage).text = "Finding server" }
        // First see if we have a saved server and that it works
        var preferences = getSharedPreferences("myflix",MODE_PRIVATE)
        var server = preferences.getString("server",null);
        if(server is String && pingServer(server)) {
            startup()
            return;
        }
        // If we're running on emulator, hardcode ip
        if(isEmulator() && pingServer("http://192.168.1.79:8000")) {
            startup()
            return
        }
        Timber.tag(Settings.TAG).d("Got no server")
        // Testing bonjour
        mServerFoundReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                var server = p1!!.getStringExtra("server")
                if(server != null && pingServer(server)) {
                    startup()
                }
            }
        }
        val filter = IntentFilter()
        filter.addAction("server_found")
        applicationContext.registerReceiver(mServerFoundReceiver, filter)
        mBonjour = NetworkDiscoveryService(applicationContext)
        /*
        val filter = IntentFilter()
        filter.addAction("server_found")
        applicationContext.registerReceiver(mServerFoundReceiver, filter)
        mBonjour = NetworkDiscoveryService(applicationContext)
        mServerFoundReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                Timber.tag(Settings.TAG).d("Server received " + p1!!.getStringExtra("server"))

                //loadStartingPage()
            }
        }*/

        /*if(server is String) {
            Timber.tag(Settings.TAG).d("Got server from preferences");
            //Settings.SERVER = server;

        } else {

            if (isEmulator()) { // Bonjour doesn't work on emulator
                Timber.tag(Settings.TAG).d("Running on emulator");
                Settings.SERVER = "http://192.168.1.79:8000"

                loadStartingPage()
            } else {
                mServerFoundReceiver = object : BroadcastReceiver() {
                    override fun onReceive(p0: Context?, p1: Intent?) {
                        Timber.tag(Settings.TAG).d("Server received " + Settings.SERVER)
                        //loadStartingPage()
                    }
                }
                val filter = IntentFilter()
                filter.addAction("server_found")
                applicationContext.registerReceiver(mServerFoundReceiver, filter)
                mBonjour = NetworkDiscoveryService(applicationContext)
            }
        //}

         */

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Start by showing the splash screen
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setupSplashScreen(splashScreen)

        // Set up some dimensions etc
        val displayMetrics = this.resources.displayMetrics
        Settings.WIDTH = displayMetrics.widthPixels.toFloat()
        Settings.HEIGHT = displayMetrics.heightPixels.toFloat()

        setContentView(R.layout.splash)
        mInstance = this;
        contentHasLoaded = true
        init()
        Timber.tag(Settings.TAG).d("onCreate done")
    }

    private fun setupSplashScreen(splashScreen: SplashScreen) {
        val content: View = findViewById(android.R.id.content)

        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (contentHasLoaded) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else false
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mServerFoundReceiver != null) {
            applicationContext.unregisterReceiver(mServerFoundReceiver)
            mServerFoundReceiver = null
        }
    }

    private fun loadStartingPage() {
        mRootView = findViewById(R.id.main_browse_fragment)
        mRootView.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { focused, direction ->
                if (direction == View.FOCUS_LEFT) {
                    findViewById(R.id.side_menu)
                } else {
                    null
                }
            }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    // Set permissions to use voice search
    private fun checkRunTimePermission() {
        val permissionArrays = arrayOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        contentHasLoaded = true
    }

    companion object {
        lateinit var mInstance: MainActivity
        fun showToast(message: String): Unit {
            mInstance.showToast(message)
        }
    }
}