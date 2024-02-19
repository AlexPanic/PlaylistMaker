package com.example.playlistmaker.domain.search.model

sealed interface SearchState {
    object HistoryClear : SearchState
    data class HistoryList(val tracks: List<Track>): SearchState
    object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    data class Empty(val message: String) : SearchState
    data class Error(val errorMessage: String) : SearchState
}