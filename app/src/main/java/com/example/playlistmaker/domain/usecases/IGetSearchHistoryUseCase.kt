package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.search.model.Track

interface IGetSearchHistoryUseCase {
    fun execute(): List<Track>
}