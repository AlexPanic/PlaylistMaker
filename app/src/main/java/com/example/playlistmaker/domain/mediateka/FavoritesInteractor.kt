package com.example.playlistmaker.domain.mediateka

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun getFavorites(): Flow<List<Track>>
    suspend fun addToFavorites(track: Track): Flow<Boolean>
    suspend fun removeFromFavorites(track: Track): Flow<Boolean>
    suspend fun isFavorite(trackID: Int): Flow<Boolean>
}