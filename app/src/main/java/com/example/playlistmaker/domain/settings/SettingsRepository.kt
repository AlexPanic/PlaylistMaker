package com.example.playlistmaker.domain.settings

import com.example.playlistmaker.domain.search.model.Track

interface SettingsRepository {
    fun getSavedDarkMode(deviceDarkModeOn: Boolean): Boolean
    fun setDarkMode(darkModeOn: Boolean)
    fun getSearchHistory(): List<Track>
    fun saveSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()
}