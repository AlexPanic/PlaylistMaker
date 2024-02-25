package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.data.search.TracksRepositoryImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.settings.ExternalNavigator
import com.example.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.player.usecase.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.settings.usecase.impl.GetDarkModeUseCaseImpl
import com.example.playlistmaker.domain.settings.usecase.GetDarkModeUseCase
import com.example.playlistmaker.domain.search.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.settings.usecase.SetDarkModeUseCase
import com.example.playlistmaker.domain.sharing.usecase.ShareAppUseCase
import com.example.playlistmaker.domain.search.usecase.impl.SaveSearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.search.usecase.impl.ClearSearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.search.usecase.impl.GetSearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.settings.usecase.impl.SetDarkModeUseCaseImpl
import com.example.playlistmaker.domain.sharing.usecase.MessageSupportUseCase
import com.example.playlistmaker.domain.sharing.usecase.UserAgreementUseCase
import com.example.playlistmaker.domain.sharing.usecase.impl.MessageSupportUseCaseImpl
import com.example.playlistmaker.domain.sharing.usecase.impl.ShareAppUseCaseImpl
import com.example.playlistmaker.domain.sharing.usecase.impl.UserAgreementUseCaseImpl

object Creator {

    private lateinit var app: Application
    fun setApplication(app: App) {
        Creator.app = app
    }

    fun provideUserAgreementUseCase(): UserAgreementUseCase {
        return UserAgreementUseCaseImpl(getExternalNavigator())
    }
    fun provideMessageSupportUseCase(): MessageSupportUseCase {
        return MessageSupportUseCaseImpl(getExternalNavigator())
    }
    fun provideShareAppUseCase(): ShareAppUseCase {
        return ShareAppUseCaseImpl(getExternalNavigator())
    }
    private fun getExternalNavigator() : ExternalNavigator {
        return ExternalNavigatorImpl(app)
    }

    fun provideSetDarkModeUseCase(): SetDarkModeUseCase {
        return SetDarkModeUseCaseImpl(provideSettingsRepository())
    }

    fun provideGetDarkModeUseCase(): GetDarkModeUseCase {
        return GetDarkModeUseCaseImpl(provideSettingsRepository())
    }

    fun provideGetSearchHistoryUseCase(): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCaseImpl(provideSettingsRepository())
    }
    fun provideClearSearchHistoryUseCase(): ClearSearchHistoryUseCase {
        return ClearSearchHistoryUseCaseImpl(provideSettingsRepository())
    }

    fun provideSaveSearchHistoryUseCase(): SaveSearchHistoryUseCase {
        return SaveSearchHistoryUseCaseImpl(provideSettingsRepository())
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(app)
    }

    private fun providePlayerControl(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerControl())
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

}