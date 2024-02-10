package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track

interface SettingsRepository {
    fun getSavedDarkMode(deviceDarkModeOn: Boolean): Boolean
    fun setDarkMode(darkModeOn: Boolean)
    fun getSearchHistory(): MutableList<Track>
    fun saveSearchHistory(tracks: List<Track>)
    fun clearSearchHistory()
}