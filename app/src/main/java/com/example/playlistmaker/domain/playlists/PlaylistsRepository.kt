package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun addPlaylist(playlist: Playlist): Flow<Long>
    fun updateCover(cover: String, id: Long): Flow<Boolean>
    fun addTrack(track: Track, playlist: Playlist): Flow<Boolean>
}