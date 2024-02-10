package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.models.Track

interface IGetSearchHistoryUseCase {
    fun execute(): MutableList<Track>
}