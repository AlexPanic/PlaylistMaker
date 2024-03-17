package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface TracksInteractor {
    fun findTracks(expression: String, consumer: (List<Track>?, String?)->Unit)
}