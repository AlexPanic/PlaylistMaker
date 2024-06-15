package com.example.playlistmaker.domain.favorites

import com.example.playlistmaker.domain.search.model.Track

sealed interface FavoritesState {
    object Loading : FavoritesState
    data class Content(val tracks: List<Track>) : FavoritesState
    data class Empty(val message: String) : FavoritesState
}