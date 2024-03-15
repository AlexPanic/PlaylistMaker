package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface TracksConsumer {
    fun consume(foundTracks: List<Track>?, errorMessage: String?)
}