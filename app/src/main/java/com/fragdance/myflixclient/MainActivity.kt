package com.fragdance.myflixclient

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.*
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.leanback.widget.BrowseFrameLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.fragdance.myflixclient.services.*
import com.fragdance.myflixclient.utils.MovieLoaders
import com.fragdance.myflixclient.utils.isEmulator
import kotlinx.coroutines.*
import timber.log.Timber


class MainView(context: Context, attrs: AttributeSet) : BrowseFrameLayout(context, attrs)

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
            //var response = movieService.getLocalMovies().execute()
            var response = movieService.getFilteredMovies(5).execute()
            if (response.isSuccessful) {
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
        } catch (e: Exception) {
            Timber.tag(Settings.TAG).d("Failed to load movies")
        }
    }

    private fun loadFilters() {
        runOnUiThread { findViewById<TextView>(R.id.splashMessage).text = "Loading filters" }
        try {
            //var response = movieService.getLocalMovies().execute()
            var response = movieService.getMovieFilters().execute()
            if (response.isSuccessful) {
                Settings.movieFilters = response.body()!!
                val intent = Intent()
                intent.action = "filters_loaded"
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
        } catch (e: Exception) {
            Timber.tag(Settings.TAG).d("Failed to load movies")
        }
    }
    // Load all the local tv shows into settings
    private fun loadTVShows() {
        runOnUiThread { findViewById<TextView>(R.id.splashMessage).text = "Loading TV-shows" }
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
        } catch (e: Exception) {

        }
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

            // Set the background color of toast
            viewGroup.setBackgroundColor(Color.parseColor("#10000000"))
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

        loadStartingPage()
    }

    // Ping the server, return false if not found
    private fun pingServer(server: String): Boolean {
        try {
            Settings.SERVER_IP = server
            val response = serverService.ping().execute()
            Settings.SERVER = "http://" + Settings.SERVER_IP + ":8000"
            return true
        } catch (e: Exception) {
            Timber.tag(Settings.TAG).d("Ping failed " + e.message)
            Settings.SERVER = ""
            return false
        }
    }

    fun init() {
        GlobalScope.launch {
            getServer()
        }

    }

    private fun loadHomePageMovies(type: String) {
        runOnUiThread { findViewById<TextView>(R.id.splashMessage).text = "Loading " + type }
        MovieLoaders.reloadMovies(type, null)
    }

    private fun startup() {
        FayeService.create()
        loadMovies()
        loadFilters()
        loadTVShows()
        checkRunTimePermission()
        loadHomePageMovies("Latest")
        loadHomePageMovies("Recommended")
        loadHomePageMovies("Boxoffice")
        loadHomePageMovies("In Progress")
        for (genre in Settings.MOVIE_GENRES) {
            loadHomePageMovies(genre)
        }
        runOnUiThread { setupView() }
    }

    // Try to retrieve the server address
    private fun getServer() {
        runOnUiThread { findViewById<TextView>(R.id.splashMessage).text = "Finding server" }
        // First see if we have a saved server and that it works
        var preferences = getSharedPreferences("myflix", MODE_PRIVATE)
        var server = preferences.getString("server", null)

        if (server is String && pingServer(server)) {
            startup()
            return
        }

        // If we're running on emulator, hardcode ip
        if (isEmulator() && pingServer("192.168.1.121")) {
            startup()
            return
        }
        Timber.tag(Settings.TAG).d("Got no server")
        // Testing bonjour
        mServerFoundReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                var server = p1!!.getStringExtra("server")
                Timber.tag(Settings.TAG).d("Got server " + server)
                if (server != null && pingServer(server)) {
                    Timber.tag(Settings.TAG).d("Yay")
                    startup()
                } else {
                    Timber.tag(Settings.TAG).d("All tries failed")
                }
            }
        }
        val filter = IntentFilter()
        filter.addAction("server_found")
        applicationContext.registerReceiver(mServerFoundReceiver, filter)
        mBonjour = NetworkDiscoveryService(applicationContext)
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
        mInstance = this
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
        fun filterMovies(filterId:Int):Unit {
            val navHostFragment =
                (mInstance as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            try {
                //var response = movieService.getLocalMovies().execute()
                Timber.tag(Settings.TAG).d("Filter id "+filterId)
                var response = movieService.getFilteredMovies(filterId).execute()
                if (response.isSuccessful) {
                    Settings.movies = response.body()!!
                    navHostFragment.navController.navigate(R.id.action_global_movies);
                    /*
                    val intent = Intent()
                    intent.action = "movies_loaded"
                    intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                    mInstance.sendBroadcast(intent)

                     */
                    //loadTVShows()
                } else {
                    Toast.makeText(
                        mInstance,
                        "Something went wrong ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Timber.tag(Settings.TAG).d("Failed to load movies")
            }
        }
    }
}