package com.example.playlistmaker

import android.content.Intent
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.TheTrackRepositoryImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.repository.TheTrackRepository
import com.example.playlistmaker.domain.usecases.GetTrackUseCase

object Creator {

    fun provideGetTrackUseCase(intent: Intent): GetTrackUseCase {
        return GetTrackUseCase(provideTheTrackRepository(), intent)
    }
    private fun provideTheTrackRepository(): TheTrackRepository {
        return TheTrackRepositoryImpl()
    }


    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

}