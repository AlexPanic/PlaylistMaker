package com.example.playlistmaker.domain.search.usecase

import com.example.playlistmaker.domain.search.model.Track

interface ISaveSearchHistoryUseCase {
    fun execute(tracks: List<Track>)
}