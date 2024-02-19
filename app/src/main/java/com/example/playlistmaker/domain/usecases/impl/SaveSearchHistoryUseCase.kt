package com.example.playlistmaker.domain.usecases.impl

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.usecases.ISaveSearchHistoryUseCase

class SaveSearchHistoryUseCase(
    private val settingsRepository: SettingsRepository
) : ISaveSearchHistoryUseCase {
    override fun execute(tracks: List<Track>) {
        settingsRepository.saveSearchHistory(tracks)
    }
}