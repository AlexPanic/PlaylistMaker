package com.example.playlistmaker.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val sharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREF_FILE, Context.MODE_PRIVATE)

    override fun getSavedDarkMode(deviceDarkModeOn: Boolean): Boolean {
        return sharedPreferences.getBoolean(Constants.DARK_MODE_LAST_SWITCH, deviceDarkModeOn)
    }

    override fun setDarkMode(darkModeOn: Boolean) {
        sharedPreferences.edit()
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