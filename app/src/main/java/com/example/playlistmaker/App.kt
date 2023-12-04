package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        var darkTheme = false
    }

    private lateinit var sharedPref:SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(getString(R.string.file_preferences), MODE_PRIVATE)
        darkTheme = sharedPref.getBoolean(getString(R.string.pref_darkmode_last), false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPref.edit()
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