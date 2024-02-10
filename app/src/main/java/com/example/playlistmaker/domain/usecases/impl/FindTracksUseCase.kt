package com.example.playlistmaker.domain.usecases.impl

import com.example.playlistmaker.domain.impl.TracksInteractImpl
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.usecases.IFindTracksUseCase

class FindTracksUseCase(private val tracksRepository: TracksRepository): IFindTracksUseCase {
    override fun execute(expression: String): List<Track> {
        return tracksRepository.findTracks(expression)
    }
}