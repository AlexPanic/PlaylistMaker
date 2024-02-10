package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.models.Track

interface ISaveSearchHistoryUseCase {
    fun execute(tracks: List<Track>)
}