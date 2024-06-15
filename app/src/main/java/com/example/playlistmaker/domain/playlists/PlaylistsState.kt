package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist

sealed interface PlaylistsState {
    object Loading: PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
    data class Empty(val message: String) : PlaylistsState
}