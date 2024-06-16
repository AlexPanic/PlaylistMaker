package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TracksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTrack(track: TracksEntity)
    @Query("SELECT * FROM tracks WHERE trackId IN (:trackIdsJoinedString)")
    fun getTracks(trackIdsJoinedString: String): Flow<List<TracksEntity>>
    @Query("SELECT SUM(trackTimeMillis) AS trackTimeMillisTotal FROM tracks WHERE trackId IN (:trackIDs)")
    fun getTimeMillisTotal(trackIDs: List<Int>): Flow<Int>
}