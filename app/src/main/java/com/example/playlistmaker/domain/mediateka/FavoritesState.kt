package com.example.playlistmaker.domain.mediateka

sealed interface FavoritesState {
    data class Error(val message: String) : FavoritesState
}