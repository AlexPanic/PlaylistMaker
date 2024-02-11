package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

interface TracksRepository {
    fun findTracks(expression: String): Resource<List<Track>>
}