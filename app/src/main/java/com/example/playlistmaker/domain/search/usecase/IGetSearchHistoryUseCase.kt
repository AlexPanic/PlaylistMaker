package com.example.playlistmaker.domain.search.usecase

import com.example.playlistmaker.domain.search.model.Track

interface IGetSearchHistoryUseCase {
    fun execute(): List<Track>
}