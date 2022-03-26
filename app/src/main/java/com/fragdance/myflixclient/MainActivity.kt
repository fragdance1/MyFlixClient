package com.fragdance.myflixclient

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.KeyEventDispatcher
import androidx.fragment.app.FragmentActivity
import androidx.leanback.widget.BrowseFrameLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.fragdance.myflixclient.components.side_menu.SideMenu
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.services.movieService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MainView(context: Context, attrs:AttributeSet):BrowseFrameLayout(context,attrs) {

}
class MainActivity: FragmentActivity() {
    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController
    private lateinit var mRootView:BrowseFrameLayout
    // Load all the local movies into settings
    private fun loadMovies( ) {
        val requestCall = movieService.getLatestMovies()
        requestCall.enqueue(object:Callback<List<IMovie>>{
            override fun onResponse(call: Call<List<IMovie>>, response: Response<List<IMovie>>) {
                if(response.isSuccessful) {
                    Settings.movies = response.body()!!
                    val intent = Intent()
                    intent.action = "movies_loaded"
                    intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                    sendBroadcast(intent)
                }else{
                    Toast.makeText(this@MainActivity, "Something went wrong ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<IMovie>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Something went wrong $t", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val displayMetrics = this.resources.displayMetrics
        Settings.WIDTH = displayMetrics.widthPixels.toFloat()
        Settings.HEIGHT = displayMetrics.heightPixels.toFloat()
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        mRootView = findViewById(R.id.main_browse_fragment)


        mRootView.onFocusSearchListener = BrowseFrameLayout.OnFocusSearchListener { focused, direction ->
            Timber.tag(Settings.TAG).d("focused "+focused)
            if(direction == View.FOCUS_LEFT) {
                findViewById<View>(R.id.side_menu)
            } else {
                Timber.tag(Settings.TAG).d("focused "+focused)
                null
            }
        }
//mRootView.onChildFocusListener = this;//BrowseFrameLayout.OnChildFocusListener {focused, direction ->}*/
        loadStartingPage()
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        var handled = super.dispatchKeyEvent(event)

        return handled;
    }
    private fun loadStartingPage() {

        navGraph.startDestination = R.id.homePage
        loadMovies()
        navController.graph = navGraph

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if(intent == null) {
            return
        }
    }
/*
    override fun onRequestFocusInDescendants(
        direction: Int,
        previouslyFocusedRect: Rect?
    ): Boolean {

        return false;
    }

    override fun onRequestChildFocus(child: View?, focused: View?) {
        Timber.tag(Settings.TAG).d("onRequestChildFocus "+focused)
    }
*/
}