package com.example.playlistmaker.domain.settings.usecase.impl

import android.util.Log
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.usecase.ISetDarkModeUseCase

class SetDarkModeUseCase(private val settingsRepository: SettingsRepository) : ISetDarkModeUseCase {
    override fun execute(darkModeOn: Boolean) {
        Log.d("mine", "execute("+darkModeOn.toString()+")")
        settingsRepository.setDarkMode(darkModeOn)
    }

}
