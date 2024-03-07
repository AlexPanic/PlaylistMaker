package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.TracksRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.player.PlayerRepository
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
    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }
    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }
}