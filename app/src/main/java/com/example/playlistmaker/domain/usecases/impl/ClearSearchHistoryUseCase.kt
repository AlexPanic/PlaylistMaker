package com.example.playlistmaker.domain.usecases.impl

import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.usecases.IClearSearchHistoryUseCase

class ClearSearchHistoryUseCase(private val settingsRepository: SettingsRepository) :
    IClearSearchHistoryUseCase {
    override fun execute() {
        settingsRepository.clearSearchHistory()
    }
}