package com.example.playlistmaker.domain.playlists

sealed interface PlaylistAddState {
    object Loading: PlaylistAddState
    object Added: PlaylistAddState
    data class Error(val message: String) : PlaylistAddState
}