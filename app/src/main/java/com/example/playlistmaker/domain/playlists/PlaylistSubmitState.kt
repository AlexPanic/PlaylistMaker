package com.example.playlistmaker.domain.playlists

sealed interface PlaylistSubmitState {
    data object Loading: PlaylistSubmitState
    data object Added: PlaylistSubmitState
    data object Updated: PlaylistSubmitState
    data class Error(val message: String) : PlaylistSubmitState
}