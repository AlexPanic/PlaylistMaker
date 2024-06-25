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

    @Query("SELECT * FROM playlists WHERE playlistId=:id LIMIT 1")
    fun getPlaylist(id: Long): Flow<PlaylistsEntity>

    @Query("SELECT * FROM playlists ORDER BY playlistId DESC")
    fun getPlaylists(): Flow<List<PlaylistsEntity>>

    @Query(
        "SELECT COUNT(*) FROM playlists WHERE (playlistTrackIDs like '%[' || :trackId || ']%')"
                + " OR (playlistTrackIDs like '%[' || :trackId || ',%')"
                + " OR (playlistTrackIDs like '%,' || :trackId || ',%')"
                + " OR (playlistTrackIDs like '%,' || :trackId || ']%')"
    )
    fun getPlaylistsMatchByTrack(trackId: Int): Int

    @Update
    fun updatePlaylist(playlist: PlaylistsEntity)

    @Query("DELETE FROM playlists WHERE playlistId=:playlistId")
    fun deletePlaylist(playlistId: Long): Int

    /*@Delete(PlaylistsEntity::class)
    fun deletePlaylist(playlist: PlaylistsEntity)*/
}