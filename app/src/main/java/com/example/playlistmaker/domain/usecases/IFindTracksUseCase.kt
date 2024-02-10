package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.models.Track

interface IFindTracksUseCase {
    fun execute(expression: String): List<Track>
}