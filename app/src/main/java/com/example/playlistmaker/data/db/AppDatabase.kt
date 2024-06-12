package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.FavoritesDao
import com.example.playlistmaker.data.db.dao.PlaylistsDao
import com.example.playlistmaker.data.db.dao.TracksDao
import com.example.playlistmaker.data.db.entity.FavoritesEntity
import com.example.playlistmaker.data.db.entity.PlaylistsEntity
import com.example.playlistmaker.data.db.entity.TracksEntity

@Database(version = 1, entities = [FavoritesEntity::class, PlaylistsEntity::class, TracksEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun tracksDao(): TracksDao
}