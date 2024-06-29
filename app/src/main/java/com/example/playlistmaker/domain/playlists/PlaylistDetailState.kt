package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist

sealed interface PlaylistDetailState {
    data object Loading: PlaylistDetailState
    data object Deleted: PlaylistDetailState
    data class Content(val playlist: Playlist, val trackTimeTotalMinutes: Int): PlaylistDetailState
}