package com.fragdance.myflixclient.components.side_menu

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.fragdance.myflixclient.MainActivity

import com.fragdance.myflixclient.R
import com.fragdance.myflixclient.Settings
import com.fragdance.myflixclient.components.menu.MenuIconView
import com.fragdance.myflixclient.components.menu.MenuLabelView
import timber.log.Timber

class SideMenuView(context:Context, attrs: AttributeSet?):LinearLayout(context,attrs) {
    lateinit var iconsView:ViewGroup
    lateinit var labelsView:ViewGroup
    var childIndex = 0;
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if(event?.action == ACTION_DOWN) {
            when (event?.keyCode) {
                KEYCODE_DPAD_RIGHT -> return closeSidePanel()
                KEYCODE_DPAD_UP -> goUp()
                KEYCODE_DPAD_DOWN -> goDown()
                KEYCODE_ENTER,KEYCODE_DPAD_CENTER -> activateMenuItem(childIndex)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    fun closeSidePanel():Boolean {
        labelsView = findViewById(R.id.menu_labels)
        labelsView.visibility = INVISIBLE;
        clearFocus();
        return true;
    }

    fun goUp() {
        if(childIndex > 0) {
            setActiveMenuItem(childIndex-1)
        }
    }
    fun goDown() {
        if(childIndex < labelsView.childCount-1) {
            setActiveMenuItem(childIndex+1);
        }
    }

    private fun activateMenuItem(index:Int) {
        val navHostFragment =
            (context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        when(index) {
            0 -> navHostFragment.navController.navigate(R.id.action_global_home)
            1 -> navHostFragment.navController.navigate(R.id.action_global_movies);
            2 -> navHostFragment.navController.navigate(R.id.action_global_tvshows);
            3 -> navHostFragment.navController.navigate(R.id.action_global_search);
        }
        closeSidePanel()
    }

    private fun setActiveMenuItem(index:Int) {
        (labelsView.children.elementAt(childIndex) as MenuLabelView).setActive(false)
        (labelsView.children.elementAt(index) as MenuLabelView).setActive(true)

        childIndex = index;
    }
    private fun createMenuItem(label:String):View {
        var tv = MenuLabelView(context);
        tv.setText(label)

        return tv;
    }
    private fun createIconView(id: Int):View {
        var iv = MenuIconView(context)
        iv.setImageResource(id)
        return iv;
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        iconsView = findViewById(R.id.menu_icons)
        var mWidth = (Settings.WIDTH*0.04).toFloat()

        iconsView.layoutParams.width = mWidth.toInt();
        labelsView = findViewById(R.id.menu_labels)


        labelsView.addView(createMenuItem("Home"))
        iconsView.addView(createIconView(R.drawable.ic_home))
        labelsView.addView(createMenuItem("Movies"))
        iconsView.addView(createIconView(R.drawable.ic_movie))
        labelsView.addView(createMenuItem("TV-series"))
        iconsView.addView(createIconView(R.drawable.ic_tv))

        labelsView.addView(createMenuItem("Search"));
        iconsView.addView(createIconView(R.drawable.ic_search_solid));

        setActiveMenuItem(0);
        onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if(b) {
                labelsView.visibility = VISIBLE;
            } else {
                labelsView.visibility = INVISIBLE;
            }
        }

    }
}
// Side panel
class SideMenu(): Fragment() {
    lateinit var mRootView:ViewGroup
    lateinit var mMenuView:ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.tag(Settings.TAG).d("Create menu "+Settings.WIDTH)
        mRootView = inflater.inflate(R.layout.side_menu, container, false) as ViewGroup
        mMenuView = mRootView.findViewById(R.id.menu)

        mMenuView?.layoutParams?.width =  (Settings.WIDTH * 0.4f).toInt()
        mMenuView?.layoutParams?.height = Settings.HEIGHT.toInt()

        return mRootView;

    }
}