package com.example.playlistmaker.domain.usecases.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.usecases.IGetSearchHistoryUseCase

class GetSearchHistoryUseCase(private val settingsRepository: SettingsRepository) :
    IGetSearchHistoryUseCase {
    override fun execute(): MutableList<Track> = settingsRepository.getSearchHistory()
}