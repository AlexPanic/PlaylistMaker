package com.example.playlistmaker.ui.settings.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.App
import com.example.playlistmaker.domain.settings.usecase.GetDarkModeUseCase
import com.example.playlistmaker.domain.settings.usecase.SetDarkModeUseCase
import com.example.playlistmaker.domain.sharing.usecase.MessageSupportUseCase
import com.example.playlistmaker.domain.sharing.usecase.ShareAppUseCase
import com.example.playlistmaker.domain.sharing.usecase.UserAgreementUseCase

class SettingsViewModel(
    getDarkModeUseCase: GetDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val shareAppUseCase: ShareAppUseCase,
    private val messageSupportUseCase: MessageSupportUseCase,
    private val userAgreementUseCase: UserAgreementUseCase,
) : ViewModel() {

    private val _state = MutableLiveData<Boolean>()
    fun observeState(): LiveData<Boolean> = _state

    init {
        _state.value = getDarkModeUseCase.execute(App.appDarkMode)
    }

    fun switchDarkTheme(darkModeOn: Boolean) {
        setDarkModeUseCase.execute(darkModeOn)
        _state.value = darkModeOn
    }

    fun shareApp() {
        shareAppUseCase.execute()
    }

    fun messageSupport() {
        messageSupportUseCase.execute()
    }

    fun showAgreement() {
        userAgreementUseCase.execute()
    }
}