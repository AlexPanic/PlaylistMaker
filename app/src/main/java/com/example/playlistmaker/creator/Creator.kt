package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.search.TracksRepositoryImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.player.usecase.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.settings.usecase.impl.GetDarkModeUseCaseImpl
import com.example.playlistmaker.domain.settings.usecase.GetDarkModeUseCase
import com.example.playlistmaker.domain.search.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.settings.usecase.SetDarkModeUseCase
import com.example.playlistmaker.domain.sharing.usecase.IShareAppUseCase
import com.example.playlistmaker.domain.search.usecase.impl.SaveSearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.search.usecase.impl.ClearSearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.search.usecase.impl.GetSearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.settings.usecase.impl.SetDarkModeUseCaseImpl
import com.example.playlistmaker.domain.sharing.usecase.IMessageSupportUseCase
import com.example.playlistmaker.domain.sharing.usecase.IUserAgreementUseCase
import com.example.playlistmaker.domain.sharing.usecase.impl.MessageSupportUseCase
import com.example.playlistmaker.domain.sharing.usecase.impl.ShareAppUseCase
import com.example.playlistmaker.domain.sharing.usecase.impl.UserAgreementUseCase

object Creator {

    private lateinit var app: Application
    fun setApplication(app: App) {
        Creator.app = app
    }

    fun provideUserAgreementUseCase(): IUserAgreementUseCase {
        return UserAgreementUseCase(getExternalNavigator())
    }
    fun provideMessageSupportUseCase(): IMessageSupportUseCase {
        return MessageSupportUseCase(getExternalNavigator())
    }
    fun provideShareAppUseCase(): IShareAppUseCase {
        return ShareAppUseCase(getExternalNavigator())
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