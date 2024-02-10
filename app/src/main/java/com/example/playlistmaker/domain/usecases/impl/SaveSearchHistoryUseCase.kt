package com.example.playlistmaker.domain.usecases.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.usecases.ISaveSearchHistoryUseCase

class SaveSearchHistoryUseCase(
    private val settingsRepository: SettingsRepository
) : ISaveSearchHistoryUseCase {
    override fun execute(tracks: List<Track>) {
        settingsRepository.saveSearchHistory(tracks)
    }
}