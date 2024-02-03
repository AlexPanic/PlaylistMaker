package com.example.playlistmaker.domain.repository

interface AppThemeRepository {
    fun isDeviceDarkModeOn(): Boolean
    fun isAppDarkModeOn(): Boolean
    fun setDarkMode(darkModeOn: Boolean)
}