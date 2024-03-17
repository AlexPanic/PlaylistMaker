package com.example.playlistmaker.domain.mediateka

import com.example.playlistmaker.domain.mediateka.model.Playlist

interface PlaylistsInteractor {
    fun getPlaylists(consumer: (List<Playlist>?, String?)->Unit)
    fun getPlaylistTracks()
}