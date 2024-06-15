package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistsEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val playlistName: String,
    val playlistDescription: String? = null,
    val playlistCover: String? = null,
    val playlistTrackIDs: String = "",
    val playlistTracksCount: Int = 0,
)
