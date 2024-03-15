package com.example.playlistmaker.domain.search

interface TracksInteractor {
    fun findTracks(expression: String, consumer: TracksConsumer)
}