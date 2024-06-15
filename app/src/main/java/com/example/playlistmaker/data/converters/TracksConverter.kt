package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TracksEntity
import com.example.playlistmaker.domain.search.model.Track

class TracksConverter {
    fun map(track: TracksEntity): Track {
        with(track) {
            return Track(
                trackId,
                trackName,
                artistName,
                trackTimeMillis,
                artworkUrl100,
                previewUrl,
                collectionName,
                releaseDate,
                primaryGenreName,
                country
            )
        }
    }

    fun map(track: Track): TracksEntity {
        with(track) {
            return TracksEntity(
                trackId,
                trackName,
                artistName,
                trackTimeMillis,
                artworkUrl100,
                previewUrl,
                collectionName,
                releaseDate,
                primaryGenreName,
                country
            )
        }
    }
}