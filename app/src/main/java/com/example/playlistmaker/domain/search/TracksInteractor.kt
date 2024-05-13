package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun findTracks(expression: String): Flow<Pair<List<Track>?, String?>>
}