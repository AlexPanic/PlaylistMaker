package com.example.playlistmaker.domain.mediateka.impl

import com.example.playlistmaker.domain.mediateka.FavoritesInteractor
import com.example.playlistmaker.domain.mediateka.FavoritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository,
) : FavoritesInteractor {
    override suspend fun getFavorites(): Flow<List<Track>> {
        return withContext(Dispatchers.IO) {
            repository.getFavorites()
        }
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