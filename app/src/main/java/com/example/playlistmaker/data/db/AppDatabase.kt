package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.FavoritesDao
import com.example.playlistmaker.data.db.entity.FavoritesEntity

@Database(version = 1, entities = [FavoritesEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
}