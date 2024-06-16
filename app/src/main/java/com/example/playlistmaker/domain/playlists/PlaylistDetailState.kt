package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist

sealed interface PlaylistDetailState {
    object Loading: PlaylistDetailState
    data class Content(val playlist: Playlist, val trackTimeTotalMinutes: Int): PlaylistDetailState
}