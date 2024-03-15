package com.example.playlistmaker.domain.mediateka

import com.example.playlistmaker.domain.mediateka.model.Playlist

interface PlaylistsConsumer {
    fun consume(playlists: List<Playlist>?, errorMessage: String?)
}