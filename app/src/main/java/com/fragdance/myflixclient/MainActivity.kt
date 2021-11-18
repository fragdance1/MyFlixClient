package com.fragdance.myflixclient

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.services.movieService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MainActivity: FragmentActivity() {
    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController
    fun loadMovies( ) {
        val requestCall = movieService.getLatestMovies();

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
        super.onCreate(savedInstanceState);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        Timber.tag(Settings.TAG).d("MainActivity.onCreate");
        setContentView(R.layout.activity_main);

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController;
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph);
        loadStartingPage()
    }

    private fun loadStartingPage() {
        Timber.tag(Settings.TAG).d("MainActivity.loadStartingPage");
        navGraph.startDestination = R.id.browseFragment
        loadMovies();
        navController.graph = navGraph
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        if(intent == null) {
            return
        }


    }
}