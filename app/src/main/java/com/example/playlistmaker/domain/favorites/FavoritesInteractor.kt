package com.example.playlistmaker.domain.favorites

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun getFavorites(): Flow<List<Track>>
    suspend fun addToFavorites(track: Track): Flow<Boolean>
    suspend fun removeFromFavorites(track: Track): Flow<Boolean>
    suspend fun isFavorite(trackID: Int): Flow<Boolean>
}