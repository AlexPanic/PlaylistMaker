package com.example.playlistmaker.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val maxTracks = 10

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

    override fun getSearchHistory(): MutableList<Track> {
        val strJson = sharedPreferences.getString(Constants.SEARCH_HISTORY_SHARED_PREF_KEY, "")
        return if (strJson!!.isNotEmpty()) {
            val type = object : TypeToken<MutableList<Track?>?>() {}.type
            return Gson().fromJson(strJson, type)
        } else {
            mutableListOf()
        }
    }

    override fun saveSearchHistory(tracks: List<Track>) {
         sharedPreferences.edit()
            .putString(Constants.SEARCH_HISTORY_SHARED_PREF_KEY, Gson().toJson(tracks))
            .apply()
    }

    override fun clearSearchHistory() {
        sharedPreferences.edit()
            .putString(Constants.SEARCH_HISTORY_SHARED_PREF_KEY, "")
            .apply()
    }

}