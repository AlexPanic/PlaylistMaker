package com.example.playlistmaker.domain.mediateka

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavorites(): Flow<List<Track>>
    fun addToFavorites(track: Track): Flow<Boolean>
    fun removeFromFavorites(track: Track): Flow<Boolean>
    fun isFavorite(trackID: Int): Flow<Boolean>
}