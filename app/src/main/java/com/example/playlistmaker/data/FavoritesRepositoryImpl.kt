package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.FavoritesConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.FavoritesEntity
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favoritesConverter: FavoritesConverter,
) : FavoritesRepository {
    override fun getFavorites(): Flow<List<Track>> = appDatabase.favoritesDao().getFavorites().map(::convertFavoritesToTracks)

    override fun addToFavorites(track: Track): Flow<Boolean> = flow {
        withContext(Dispatchers.IO) {
            val maxId = appDatabase.favoritesDao().getMaxId()
            val favTrack = favoritesConverter.map(track, maxId)
            appDatabase.favoritesDao().addToFavorites(favTrack)
        }
        emit(true)
    }

    override fun removeFromFavorites(track: Track): Flow<Boolean> = flow {
        withContext(Dispatchers.IO) {
            val favTrack = favoritesConverter.map(track, null)
            appDatabase.favoritesDao().removeFromFavorites(favTrack)
        }
        emit(true)
    }

    override fun isFavorite(trackID: Int): Flow<Boolean> = flow {
        emit(withContext(Dispatchers.IO) {
            appDatabase.favoritesDao().isFavorite(trackID) > 0
        })
    }

    private fun convertFavoritesToTracks(favorites: List<FavoritesEntity>): List<Track> {
        return favorites.map { fav -> favoritesConverter.map(fav) }
    }
}