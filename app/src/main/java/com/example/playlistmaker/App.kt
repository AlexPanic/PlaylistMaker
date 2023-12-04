package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        private var sharedPreferences : SharedPreferences ?= null
        fun setSharedPreferences(sharedPrefs: SharedPreferences) {
            sharedPreferences = sharedPrefs
        }
        fun getSharedPreferences(): SharedPreferences {
            return sharedPreferences!!
        }
        var darkTheme = false
    }

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(getString(R.string.file_preferences), MODE_PRIVATE)
        setSharedPreferences(sharedPrefs)
        darkTheme = sharedPrefs.getBoolean(getString(R.string.pref_darkmode_last), false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPreferences!!.edit()
            .putBoolean(getString(R.string.pref_darkmode_last), darkThemeEnabled)
            .apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}