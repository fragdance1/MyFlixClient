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

class MainView(context: Context, attrs: AttributeSet) : BrowseFrameLayout(context, attrs) {

}

class MainActivity : FragmentActivity() {
    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController
    private lateinit var mRootView: BrowseFrameLayout
    private lateinit var mBonjour: NetworkDiscoveryService
    private var mServerFoundReceiver: BroadcastReceiver? = null
    var contentHasLoaded = false
    private fun reloadMovies() {
        val requestCall = movieService.getLocalMovies()
        requestCall.enqueue(object : Callback<List<IMovie>> {
            override fun onResponse(call: Call<List<IMovie>>, response: Response<List<IMovie>>) {
                if (response.isSuccessful) {

                    Timber.tag(Settings.TAG).d("Movies reloaded");
                    Settings.movies = response.body()!!
                    val intent = Intent()
                    intent.action = "movies_loaded"
                    intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                    sendBroadcast(intent)
                }
            }

            override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "reloadMovies Something went wrong $t", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
    // Load all the local movies into settings
    private fun loadMovies() {
        val requestCall = movieService.getLocalMovies()
        requestCall.enqueue(object : Callback<List<IMovie>> {
            override fun onResponse(call: Call<List<IMovie>>, response: Response<List<IMovie>>) {
                if (response.isSuccessful) {
                    var editor = getSharedPreferences("myflix",MODE_PRIVATE).edit();
                    editor.putString("server",Settings.SERVER)
                    editor.commit()
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
                Toast.makeText(this@MainActivity, "Loadmovies Something went wrong $t", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    // Load all the local tv shows into settings
    private fun loadTVShows() {
        val requestCall = tvShowService.getShows()
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
                        "loadTVShows Something went wrong ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<ITVShow>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "loadTVShows Something went wrong $t", Toast.LENGTH_LONG)
                    .show()
            }
        })
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

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag(Settings.TAG).d("MainActivity.onCreate")
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setupSplashScreen(splashScreen)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.loading_progress).visibility = View.INVISIBLE
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        //navGraph.startDestination = R.id.splashFragment
        navController.graph = navGraph
    //setContentView(R.layout.splash)

        Timber.tag(Settings.TAG).d("MainActivity.onCreate")
        mInstance = this;

        FayeService.create()

        // Get window dimensions
        //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val displayMetrics = this.resources.displayMetrics
        Settings.WIDTH = displayMetrics.widthPixels.toFloat()
        Settings.HEIGHT = displayMetrics.heightPixels.toFloat()
        var preferences = getSharedPreferences("myflix",MODE_PRIVATE)
        //SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        var server = preferences.getString("server",null);
        if(server is String) {
            Timber.tag(Settings.TAG).d("Got server from preferences");
            //Settings.SERVER = server;
            loadStartingPage();
        } else {

            if (isEmulator()) { // Bonjour doesn't work on emulator
                Timber.tag(Settings.TAG).d("Running on emulator");
                Settings.SERVER = "http://192.168.1.121:8000"
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
        checkRunTimePermission()


        Timber.tag(Settings.TAG).d("MainActivity.onCreate done")
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

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideBack = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.TRANSLATION_X,
                0f,
                -splashScreenView.view.width.toFloat()
            ).apply {
                interpolator = DecelerateInterpolator()
                duration = 800L
                doOnEnd { splashScreenView.remove() }
            }

            slideBack.start()
        }
    }
    override fun onDestroy() {
        super.onDestroy()

        if (mServerFoundReceiver != null) {
            applicationContext.unregisterReceiver(mServerFoundReceiver)
            mServerFoundReceiver = null
        }
    }

    private fun loadStartingPage() {
        /*
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.loading_progress).visibility = View.INVISIBLE
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

         */
        mRootView = findViewById(R.id.main_browse_fragment)
        mRootView.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { focused, direction ->
                if (direction == View.FOCUS_LEFT) {
                    findViewById(R.id.side_menu)
                } else {
                    null
                }
            }
       // navGraph.startDestination = R.id.homePage
        loadMovies()
        loadTVShows()
        navGraph.startDestination = R.id.homePage
        //navController.graph = navGraph


/*
        if(!FayeService.subscribe("/torrent/test") { message ->
                run {
                    val json = JSONObject(message) // String instance holding the above json
                    val status = json.getString("event")
                    Timber.tag(Settings.TAG).d("Got a message "+status)
                }
            }) {
            Timber.tag(Settings.TAG).d("Couldn subscribe")
        } else {
            Timber.tag(Settings.TAG).d("Subscribed")
        }
*/
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    // Search stuff
    private fun checkRunTimePermission() {
        Timber.tag(Settings.TAG).d("==== checkRuntime Permission")

        val permissionArrays = arrayOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111)
        } else {
            Timber.tag(Settings.TAG).d( "==== checkRuntime Permission else")
            //mFragment.setRecognitionListener()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.tag(Settings.TAG).d( "==== OnPermission Result")

        if (11111 == requestCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
            }
            //mFragment.setRecognitionListener()
        }
        contentHasLoaded = true
    }

    companion object {
        lateinit var mInstance: MainActivity
        fun showToast(message: String): Unit {
            mInstance.showToast(message)
        }

    }

}