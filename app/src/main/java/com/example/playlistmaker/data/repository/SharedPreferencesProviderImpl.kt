package com.example.playlistmaker.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.domain.repository.SharedPreferencesProvider

class SharedPreferencesProviderImpl(private val context: Context) : SharedPreferencesProvider {
    override fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(Constants.SHARED_PREF_FILE, Context.MODE_PRIVATE)
    }
}
