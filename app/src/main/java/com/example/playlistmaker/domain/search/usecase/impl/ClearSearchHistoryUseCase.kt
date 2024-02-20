package com.example.playlistmaker.domain.search.usecase.impl

import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.search.usecase.IClearSearchHistoryUseCase

class ClearSearchHistoryUseCase(private val settingsRepository: SettingsRepository) :
    IClearSearchHistoryUseCase {
    override fun execute() {
        settingsRepository.clearSearchHistory()
    }
}