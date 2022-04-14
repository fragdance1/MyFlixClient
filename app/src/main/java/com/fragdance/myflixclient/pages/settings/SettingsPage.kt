package com.fragdance.myflixclient.pages.settings

import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.leanback.preference.LeanbackSettingsFragment
import androidx.leanback.preference.LeanbackSettingsFragmentCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.fragdance.myflixclient.R

class SettingsPage: LeanbackSettingsFragmentCompat() {
    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        return false
    }

    override fun onPreferenceStartScreen(
        caller: PreferenceFragmentCompat,
        pref: PreferenceScreen
    ): Boolean {
        val frag = buildPreferenceFragment(R.xml.prefs,pref.key)
        startPreferenceFragment(frag)
        return true
    }

    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(buildPreferenceFragment(R.xml.prefs,null))
    }

    private fun buildPreferenceFragment(resId:Int,root:String?):PreferenceFragmentCompat {
        var prefFragment = PrefFragment()
        var args = Bundle()
        args.putInt("preferenceResource",resId)
        args.putString("root",root)
        prefFragment.arguments = args;
        return prefFragment
    }

    companion object {
        class PrefFragment:LeanbackPreferenceFragmentCompat() {
            override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
                val root = arguments?.getString("root")
                val resId = arguments?.getInt("preferenceResource") as Int
                setPreferencesFromResource(resId!!,root)

            }

        }
    }
}