package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.data.db.entity.TracksEntity

@Dao
interface TracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTrack(track: TracksEntity)
}