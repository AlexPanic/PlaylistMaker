package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.App
import com.example.playlistmaker.creator.Creator

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }
    private val _state = MutableLiveData<Boolean>()
    fun observeState(): LiveData<Boolean> = _state

    private val getDarkModeUseCase by lazy { Creator.provideGetDarkModeUseCase() }
    private val setDarkModeUseCase by lazy { Creator.provideSetDarkModeUseCase() }
    private val shareAppUseCase by lazy { Creator.provideShareAppUseCase() }
    private val messageSupportUseCase by lazy { Creator.provideMessageSupportUseCase() }
    private val userAgreementUseCase by lazy { Creator.provideUserAgreementUseCase() }

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