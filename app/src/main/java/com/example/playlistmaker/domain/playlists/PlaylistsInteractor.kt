package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getTracks(trackIDs: List<Int>): Flow<List<Track>>
    suspend fun getPlaylist(id: Long): Flow<Playlist>
    suspend fun getTrackTimeMillisTotal(trackIDs: List<Int>): Flow<Int>
    suspend fun addPlaylist(playlist: Playlist): Flow<Long>
    suspend fun updatePlaylist(playlist: Playlist): Flow<Boolean>
    suspend fun updateCover(cover: String, id: Long): Flow<Boolean>
    suspend fun addTrack(track: Track, playlist: Playlist): Flow<Boolean>
    suspend fun removeTrack(trackId: Int, playlistId: Long): Flow<List<Int>>
    suspend fun deletePlaylist(playlist: Playlist): Flow<Boolean>
}