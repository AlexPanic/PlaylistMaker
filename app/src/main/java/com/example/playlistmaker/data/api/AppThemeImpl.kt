package com.example.playlistmaker.data.api

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.domain.repository.AppTheme

class AppThemeImpl(context: Context) : AppTheme {
    private val sharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREF_FILE, AppCompatActivity.MODE_PRIVATE)

    override fun checkSavedTheme(defaultStateOfDarkTheme: Boolean): Boolean {
        return sharedPreferences.getBoolean(Constants.DARK_MODE_LAST_SWITCH, defaultStateOfDarkTheme)
    }

    override fun saveTheme(darkThemeEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(Constants.DARK_MODE_LAST_SWITCH, darkThemeEnabled)
            .apply()
    }
}