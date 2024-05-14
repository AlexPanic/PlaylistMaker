package com.example.playlistmaker.domain.search

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun findTracks(expression: String): Flow<Resource<List<Track>>>
}