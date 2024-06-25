package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.PlaylistsConverter
import com.example.playlistmaker.data.converters.TracksConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistsEntity
import com.example.playlistmaker.data.db.entity.TracksEntity
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistsConverter: PlaylistsConverter,
    private val tracksConverter: TracksConverter,
) : PlaylistsRepository {
    override fun getPlaylists(): Flow<List<Playlist>> =
        appDatabase.playlistsDao().getPlaylists().map(::convertPlaylists)

    override fun getTracks(trackIDs: List<Int>): Flow<List<Track>> =
        appDatabase.tracksDao().getTracks(trackIDs).map(::convertTracks)

    override fun getPlaylist(playlistId: Long): Flow<Playlist> =
        appDatabase.playlistsDao().getPlaylist(playlistId).map {
            playlistsConverter.map(it)
        }

    override fun getTrackTimeMillisTotal(trackIDs: List<Int>): Flow<Int> =
        appDatabase.tracksDao().getTimeMillisTotal(trackIDs)

    override fun addPlaylist(playlist: Playlist): Flow<Long> = flow {
        val playlistId = withContext(Dispatchers.IO) {
            appDatabase.playlistsDao().addPlaylist(playlistsConverter.map(playlist))
        }
        emit(playlistId)
    }

    override fun updatePlaylist(playlist: Playlist): Flow<Boolean> = flow {
        withContext(Dispatchers.IO) {
            appDatabase.playlistsDao().updatePlaylist(playlistsConverter.map(playlist))
        }
        emit(true)
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

    override fun removeTrack(trackId: Int, playlistId: Long): Flow<List<Int>> = flow {

        getPlaylist(playlistId).collect { playlist ->
            playlist.trackIDs.remove(trackId)
            playlist.tracksCount = playlist.trackIDs.size
            withContext(Dispatchers.IO) {
                appDatabase.playlistsDao().updatePlaylist(playlistsConverter.map(playlist))
                val matches = appDatabase.playlistsDao().getPlaylistsMatchByTrack(trackId)
                if (matches == 0) {
                    appDatabase.tracksDao().deleteTrack(trackId)
                }
            }
            emit(playlist.trackIDs)
        }
    }

    /*
            //val trackIDs = playlist.trackIDs
            withContext(Dispatchers.IO) {
                appDatabase.playlistsDao().deletePlaylist(playlistsConverter.map(playlist))
                /*if (trackIDs.isNotEmpty()) {
                    for (trackId in trackIDs) {
                        val matches = appDatabase.playlistsDao().getPlaylistsMatchByTrack(trackId)
                        if (matches == 0) {
                            appDatabase.tracksDao().deleteTrack(trackId)
                        }
                    }
                }*/
            }

     */

    override fun deletePlaylist(playlist: Playlist) =
        appDatabase.playlistsDao().deletePlaylist(playlist.id)


    private fun convertPlaylists(playlistEntities: List<PlaylistsEntity>): List<Playlist> =
        playlistEntities.map { playlistEntity -> playlistsConverter.map(playlistEntity) }


    private fun convertTracks(trackEntities: List<TracksEntity>): List<Track> =
        trackEntities.map { trackEntity -> tracksConverter.map(trackEntity) }
}