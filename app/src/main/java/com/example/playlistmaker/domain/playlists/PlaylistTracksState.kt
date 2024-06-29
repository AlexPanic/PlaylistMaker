package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.search.model.Track

sealed interface PlaylistTracksState {
    object Loading : PlaylistTracksState
    object Empty: PlaylistTracksState
    data class Content(val tracks: List<Track>) : PlaylistTracksState
}