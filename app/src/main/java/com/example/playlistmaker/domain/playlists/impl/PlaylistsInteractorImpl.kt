package com.example.playlistmaker.domain.playlists.impl

import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository,
) : PlaylistsInteractor {
    override suspend fun getPlaylists(): Flow<List<Playlist>> = withContext(Dispatchers.IO) {
        repository.getPlaylists()
    }

    override suspend fun getPlaylist(id: Long): Flow<Playlist> = withContext(Dispatchers.IO) {
        repository.getPlaylist(id)
    }

    override suspend fun getTrackTimeMillisTotal(trackIDs: List<Int>): Flow<Int> = withContext(Dispatchers.IO) {
        repository.getTrackTimeMillisTotal(trackIDs)
    }

    override suspend fun addPlaylist(playlist: Playlist): Flow<Long> {
        return repository.addPlaylist(playlist)
    }

    override suspend fun updateCover(cover: String, id: Long): Flow<Boolean> {
        return repository.updateCover(cover, id)
    }

    override suspend fun addTrack(track: Track, playlist: Playlist): Flow<Boolean> {
        return repository.addTrack(track, playlist)
    }


}