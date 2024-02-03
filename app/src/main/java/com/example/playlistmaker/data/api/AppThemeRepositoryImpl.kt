package com.example.playlistmaker.data.api

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.domain.repository.AppThemeRepository
import com.example.playlistmaker.domain.repository.SharedPreferencesProvider

class AppThemeRepositoryImpl(private val sharedPreferencesProvider: SharedPreferencesProvider) :
    AppThemeRepository {
    override fun isDeviceDarkModeOn(): Boolean {
        val nightMode = AppCompatDelegate.getDefaultNightMode()
        return nightMode == AppCompatDelegate.MODE_NIGHT_YES
    }

    override fun isAppDarkModeOn(): Boolean {
        val sharedPref = sharedPreferencesProvider.getSharedPreferences()
        return sharedPref.getBoolean(Constants.DARK_MODE_LAST_SWITCH, false)
    }

    override fun setDarkMode(darkModeOn: Boolean) {
        val sharedPref = sharedPreferencesProvider.getSharedPreferences()
        sharedPref.edit()
            .putBoolean(Constants.DARK_MODE_LAST_SWITCH, darkModeOn)
            .apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkModeOn) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}