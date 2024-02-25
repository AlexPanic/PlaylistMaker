package com.example.playlistmaker.domain.search.usecase.impl

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.search.usecase.GetSearchHistoryUseCase

class GetSearchHistoryUseCaseImpl(private val settingsRepository: SettingsRepository) :
    GetSearchHistoryUseCase {
    override fun execute(): List<Track> = settingsRepository.getSearchHistory()
}