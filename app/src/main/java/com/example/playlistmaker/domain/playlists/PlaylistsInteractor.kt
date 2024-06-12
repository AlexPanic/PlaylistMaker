package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist): Flow<Long>
    suspend fun updateCover(cover: String, id: Long): Flow<Boolean>
    suspend fun addTrack(track: Track, playlist: Playlist): Flow<Boolean>
}