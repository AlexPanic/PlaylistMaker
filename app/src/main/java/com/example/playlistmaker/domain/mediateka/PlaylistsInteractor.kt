package com.example.playlistmaker.domain.mediateka

interface PlaylistsInteractor {
    fun getPlaylists(consumer: PlaylistsConsumer)
    fun getPlaylistTracks()
}