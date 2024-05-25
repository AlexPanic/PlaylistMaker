package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.FavoritesEntity
import com.example.playlistmaker.domain.search.model.Track

class FavoritesConverter {
    fun map(fav: FavoritesEntity): Track {
        with(fav) {
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

    fun map(track: Track, maxId: Int?): FavoritesEntity {
        with(track) {
            return FavoritesEntity(
                trackId,
                trackName,
                artistName,
                trackTimeMillis,
                artworkUrl100,
                previewUrl,
                collectionName,
                releaseDate,
                primaryGenreName,
                country,
                if (maxId==null) 1 else maxId+1
            )
        }
    }

}