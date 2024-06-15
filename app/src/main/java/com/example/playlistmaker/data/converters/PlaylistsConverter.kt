package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistsEntity
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistsConverter {
    fun map(pl: PlaylistsEntity): Playlist {
        val type = object : TypeToken<MutableList<Int?>?>() {}.type
        with(pl) {
            return Playlist(
                playlistId,
                playlistName,
                playlistDescription,
                playlistCover,
                Gson().fromJson(playlistTrackIDs, type),
                playlistTracksCount
            )
        }
    }

    fun map(pl: Playlist): PlaylistsEntity {
        with(pl) {
            return PlaylistsEntity(
                id,
                name,
                description,
                cover,
                Gson().toJson(trackIDs),
                tracksCount
            )
        }
    }
}