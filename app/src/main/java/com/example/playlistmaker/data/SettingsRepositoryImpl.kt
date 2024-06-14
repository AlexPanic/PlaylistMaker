package com.example.playlistmaker.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    companion object {
        private const val SEARCH_HISTORY_SHARED_PREF_KEY = "SearchHistoryKey"
        private const val SHARED_PREF_FILE = "playlist_maker_pref"
        private const val DARK_MODE_LAST_SWITCH = "darkmode_last_switch"
    }

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE)

    override fun getSavedDarkMode(deviceDarkModeOn: Boolean): Boolean {
        return sharedPreferences.getBoolean(
            DARK_MODE_LAST_SWITCH,
            false
        )
    }

    override fun setDarkMode(darkModeOn: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_MODE_LAST_SWITCH, darkModeOn)
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
        val strJson = sharedPreferences.getString(SEARCH_HISTORY_SHARED_PREF_KEY, "")
        return if (strJson!!.isNotEmpty()) {
            val type = object : TypeToken<MutableList<Track?>?>() {}.type
            return Gson().fromJson(strJson, type)
        } else {
            mutableListOf()
        }
    }

    override fun saveSearchHistory(tracks: List<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_SHARED_PREF_KEY, Gson().toJson(tracks))
            .apply()
    }

    override fun clearSearchHistory() {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_SHARED_PREF_KEY, "")
            .apply()
    }

}