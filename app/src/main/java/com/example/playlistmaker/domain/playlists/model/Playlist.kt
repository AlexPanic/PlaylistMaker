package com.example.playlistmaker.domain.playlists.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String? = null,
    val cover: String? = null,
    val trackIDs: MutableList<Int> = mutableListOf(),
    var tracksCount: Int,
)
