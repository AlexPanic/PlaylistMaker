package com.example.playlistmaker

import com.example.playlistmaker.data.api.PlayerControlImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.PlayerControl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.usecases.ControlPlayerUseCase

object Creator {
    fun provideControlPlayerUseCase(): ControlPlayerUseCase {
        return ControlPlayerUseCase(providePlayerControl())
    }
    private fun providePlayerControl(): PlayerControl {
        return PlayerControlImpl()
    }
    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

}