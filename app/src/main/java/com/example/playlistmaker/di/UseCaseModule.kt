package com.example.playlistmaker.di

import com.example.playlistmaker.domain.search.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.impl.ClearSearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.search.usecase.impl.GetSearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.search.usecase.impl.SaveSearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.settings.usecase.GetDarkModeUseCase
import com.example.playlistmaker.domain.settings.usecase.SetDarkModeUseCase
import com.example.playlistmaker.domain.settings.usecase.impl.GetDarkModeUseCaseImpl
import com.example.playlistmaker.domain.settings.usecase.impl.SetDarkModeUseCaseImpl
import com.example.playlistmaker.domain.sharing.usecase.MessageSupportUseCase
import com.example.playlistmaker.domain.sharing.usecase.ShareAppUseCase
import com.example.playlistmaker.domain.sharing.usecase.UserAgreementUseCase
import com.example.playlistmaker.domain.sharing.usecase.impl.MessageSupportUseCaseImpl
import com.example.playlistmaker.domain.sharing.usecase.impl.ShareAppUseCaseImpl
import com.example.playlistmaker.domain.sharing.usecase.impl.UserAgreementUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {

    // История поиска
    single<GetSearchHistoryUseCase> {
        GetSearchHistoryUseCaseImpl(get())
    }
    single<SaveSearchHistoryUseCase> {
        SaveSearchHistoryUseCaseImpl(get())
    }
    single<ClearSearchHistoryUseCase> {
        ClearSearchHistoryUseCaseImpl(get())
    }

    // Темная тема
    single<GetDarkModeUseCase> {
        GetDarkModeUseCaseImpl(get())
    }
    single<SetDarkModeUseCase> {
        SetDarkModeUseCaseImpl(get())
    }

    // Интеракции в настройках
    single<ShareAppUseCase> {
        ShareAppUseCaseImpl(get())
    }
    single<MessageSupportUseCase> {
        MessageSupportUseCaseImpl(get())
    }
    single<UserAgreementUseCase> {
        UserAgreementUseCaseImpl(get())
    }
}