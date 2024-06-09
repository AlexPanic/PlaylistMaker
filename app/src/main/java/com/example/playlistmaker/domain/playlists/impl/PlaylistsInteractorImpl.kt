package com.example.playlistmaker.domain.playlists.impl

import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository,
) : PlaylistsInteractor {
    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return withContext(Dispatchers.IO) {
            repository.getPlaylists()
        }
    }

    override suspend fun addPlaylist(playlist: Playlist): Flow<Long> {
        return repository.addPlaylist(playlist)
    }

    override suspend fun updateCover(cover: String, id: Long): Flow<Boolean> {
        return repository.updateCover(cover, id)
    }


}