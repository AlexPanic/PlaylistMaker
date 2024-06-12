package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.PlaylistsConverter
import com.example.playlistmaker.data.converters.TracksConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistsEntity
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistsConverter: PlaylistsConverter,
    private val tracksConverter: TracksConverter,
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

    override fun addTrack(track: Track, playlist: Playlist): Flow<Boolean> = flow {
        playlist.trackIDs.add(track.trackId)
        playlist.tracksCount = playlist.trackIDs.size
        withContext(Dispatchers.IO) {
            val playlistEntity = playlistsConverter.map(playlist)
            appDatabase.playlistsDao().updatePlaylist(playlistEntity)
            val tracksEntity = tracksConverter.map(track)
            appDatabase.tracksDao().addTrack(track = tracksEntity)
        }
        emit(true)
    }

    private fun convertPlaylists(playlistEntities: List<PlaylistsEntity>): List<Playlist> {
        return playlistEntities.map { playlistEntity -> playlistsConverter.map(playlistEntity) }
    }
}