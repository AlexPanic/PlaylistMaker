package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun getSavedDarkMode(deviceDarkModeOn: Boolean): Boolean
    fun setDarkMode(darkModeOn: Boolean)
}