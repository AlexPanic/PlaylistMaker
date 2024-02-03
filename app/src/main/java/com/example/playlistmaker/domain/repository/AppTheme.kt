package com.example.playlistmaker.domain.repository

interface AppTheme {
    fun checkSavedTheme(defaultStateOfDarkTheme: Boolean): Boolean
    fun saveTheme(darkThemeEnabled: Boolean)
}