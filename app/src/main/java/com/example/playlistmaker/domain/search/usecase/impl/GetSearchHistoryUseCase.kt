package com.example.playlistmaker.domain.search.usecase.impl

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.search.usecase.IGetSearchHistoryUseCase

class GetSearchHistoryUseCase(private val settingsRepository: SettingsRepository) :
    IGetSearchHistoryUseCase {
    override fun execute(): List<Track> = settingsRepository.getSearchHistory()
}