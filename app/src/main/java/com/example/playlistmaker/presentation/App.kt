package com.example.playlistmaker.presentation

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.core.Constants

class App : Application() {

    companion object {
        var darkTheme = false
    }

    private var sharedPref:SharedPreferences?=null

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE)
        darkTheme = sharedPref!!.getBoolean(Constants.DARK_MODE_LAST_SWITCH, false)
        // если на устройстве включен общий темный режим
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                if (!darkTheme) darkTheme = true
            }
        }
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPref!!.edit()
            .putBoolean(Constants.DARK_MODE_LAST_SWITCH, darkThemeEnabled)
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