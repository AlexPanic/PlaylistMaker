package com.example.playlistmaker.di

import com.example.playlistmaker.ui.mediateka.view_model.FavoritesViewModel
import com.example.playlistmaker.ui.mediateka.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PlayerViewModel(get(), get())
    }
    viewModel {
        SearchViewModel(get(), androidContext(), get(), get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get(), get(), get(), get())
    }

    viewModel {
        FavoritesViewModel(androidContext(), get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }
}