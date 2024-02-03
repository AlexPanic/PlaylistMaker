package com.example.playlistmaker.domain.repository

import android.content.SharedPreferences

interface SharedPreferencesProvider {
    fun getSharedPreferences(): SharedPreferences
}