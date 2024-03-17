package com.example.playlistmaker.domain.mediateka

sealed interface PlaylistsState {
    data class Content(val param: String) : PlaylistsState
    data class Error(val message: String) : PlaylistsState
}