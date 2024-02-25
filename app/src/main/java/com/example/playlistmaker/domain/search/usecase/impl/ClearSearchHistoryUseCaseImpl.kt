package com.example.playlistmaker.domain.search.usecase.impl

import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.search.usecase.ClearSearchHistoryUseCase

class ClearSearchHistoryUseCaseImpl(private val settingsRepository: SettingsRepository) :
    ClearSearchHistoryUseCase {
    override fun execute() {
        settingsRepository.clearSearchHistory()
    }
}