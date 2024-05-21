package com.example.playlistmaker.domain.mediateka.impl

import com.example.playlistmaker.domain.mediateka.FavoritesInteractor
import com.example.playlistmaker.domain.mediateka.FavoritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository,
) : FavoritesInteractor {
    override fun getFavorites(): Flow<List<Track>> {
        return repository.getFavorites()
    }

    override suspend fun addToFavorites(track: Track): Flow<Boolean> {
        return repository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track): Flow<Boolean> {
        return repository.removeFromFavorites(track)
    }

    override suspend fun isFavorite(trackID: Int): Flow<Boolean> {
        return repository.isFavorite(trackID)
    }

}