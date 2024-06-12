package com.example.playlistmaker.domain.playlists

sealed interface AddTrackToPlaylistState {
    data class AlreadyContains(val playlistName: String): AddTrackToPlaylistState
    data class Added(val playlistName: String): AddTrackToPlaylistState
}