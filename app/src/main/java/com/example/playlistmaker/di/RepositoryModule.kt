package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.FavoritesRepositoryImpl
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.SettingsRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.converters.FavoritesConverter
import com.example.playlistmaker.data.converters.PlaylistsConverter
import com.example.playlistmaker.data.converters.TracksConverter
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }
    factory {
        MediaPlayer()
    }
    factory { FavoritesConverter() }
    factory { PlaylistsConverter() }
    factory { TracksConverter() }
    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }
    single<TracksRepository> {
        TracksRepositoryImpl(get(), androidContext())
    }
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get())
    }
}