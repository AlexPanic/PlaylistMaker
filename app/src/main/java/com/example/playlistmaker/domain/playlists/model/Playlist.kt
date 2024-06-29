package com.example.playlistmaker.domain.playlists.model

data class Playlist(
    val id: Long,
    var name: String,
    var description: String? = null,
    var cover: String? = null,
    var trackIDs: MutableList<Int> = mutableListOf(),
    var tracksCount: Int,
)
