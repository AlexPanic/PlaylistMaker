package com.example.playlistmaker.domain.repository

import android.content.SharedPreferences

interface SharedPreferencesRepository {
    fun getSharedPreferences(): SharedPreferences
}