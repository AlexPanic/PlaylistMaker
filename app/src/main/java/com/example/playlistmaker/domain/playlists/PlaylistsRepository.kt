package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getTracks(trackIDs: List<Int>): Flow<List<Track>>
    fun getPlaylist(playlistId: Long): Flow<Playlist>
    fun getTrackTimeMillisTotal(trackIDs: List<Int>): Flow<Int>
    fun addPlaylist(playlist: Playlist): Flow<Long>
    fun updatePlaylist(playlist: Playlist): Flow<Boolean>
    fun updateCover(cover: String, id: Long): Flow<Boolean>
    fun addTrack(track: Track, playlist: Playlist): Flow<Boolean>
    fun removeTrack(trackId: Int, playlistId: Long): Flow<List<Int>>
    fun deletePlaylist(playlistId: Long): Flow<Boolean>
}