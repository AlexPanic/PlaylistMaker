package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.creator.Resource

interface TracksRepository {
    fun findTracks(expression: String): Resource<List<Track>>
}