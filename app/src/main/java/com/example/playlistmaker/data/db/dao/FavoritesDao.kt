package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.FavoritesEntity

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToFavorites(track: FavoritesEntity)

    @Delete
    fun removeFromFavorites(track: FavoritesEntity)

    @Query("SELECT * FROM favorites ORDER BY id DESC")
    fun getFavorites(): List<FavoritesEntity>

    @Query("SELECT MAX(id) AS maxId FROM favorites")
    fun getMaxId(): Int?

    @Query("SELECT COUNT(*) FROM favorites WHERE trackId= :trackID")
    fun isFavorite(trackID: Int): Int
}