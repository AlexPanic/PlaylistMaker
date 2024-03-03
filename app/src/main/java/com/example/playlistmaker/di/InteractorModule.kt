package com.example.playlistmaker.di

import com.example.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.usecase.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    single<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }
}