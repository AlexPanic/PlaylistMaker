package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.PlaylistsConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistsEntity
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistsConverter: PlaylistsConverter,
) : PlaylistsRepository {
    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        appDatabase.playlistsDao().getPlaylists().collect {
            emit(convertPlaylists(it))
        }
    }

    override fun addPlaylist(playlist: Playlist): Flow<Long> = flow {
        val playlistId = withContext(Dispatchers.IO) {
            appDatabase.playlistsDao().addPlaylist(playlistsConverter.map(playlist))
        }
        emit(playlistId)
    }

    override fun updateCover(cover: String, id: Long): Flow<Boolean> = flow {
        withContext(Dispatchers.IO) {
            appDatabase.playlistsDao().updatePlaylistCover(cover, id)
        }
        emit(true)
    }

    private fun convertPlaylists(playlistEntities: List<PlaylistsEntity>): List<Playlist> {
        return playlistEntities.map { playlistEntity -> playlistsConverter.map(playlistEntity) }
    }
}