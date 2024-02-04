package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun isDeviceDarkModeOn(): Boolean
    fun isAppDarkModeOn(): Boolean
    fun setDarkMode(darkModeOn: Boolean)
}