package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.PlaylistsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlaylist(playlist: PlaylistsEntity): Long

    @Query("UPDATE playlists SET playlistCover=:cover WHERE playlistId=:id")
    fun updatePlaylistCover(cover: String, id: Long)

    @Query("UPDATE playlists SET playlistTrackIDs=:trackIDs WHERE playlistId=:id")
    fun updateTrackIDs(trackIDs: String, id: Long)

    @Update
    fun updatePlaylist(playlist: PlaylistsEntity)

    @Query("SELECT * FROM playlists ORDER BY playlistId DESC")
    fun getPlaylists(): Flow<List<PlaylistsEntity>>
}