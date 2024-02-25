package com.example.playlistmaker.domain.search.usecase.impl

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.search.usecase.SaveSearchHistoryUseCase

class SaveSearchHistoryUseCaseImpl(
    private val settingsRepository: SettingsRepository
) : SaveSearchHistoryUseCase {
    override fun execute(tracks: List<Track>) {
        settingsRepository.saveSearchHistory(tracks)
    }
}