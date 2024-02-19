package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.search.model.Track

interface ISaveSearchHistoryUseCase {
    fun execute(tracks: List<Track>)
}