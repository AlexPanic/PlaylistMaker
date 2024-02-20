package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.model.SettingsState
import com.example.playlistmaker.creator.App

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

    private val stateLiveData = MutableLiveData<SettingsState>()
    init {
        stateLiveData.value = SettingsState.Default
    }

    fun observeState(): LiveData<SettingsState> = stateLiveData

    private val setDarkModeUseCase by lazy { Creator.provideSetDarkModeUseCase()}
    private val shareAppUseCase by lazy { Creator.provideShareAppUseCase() }
    private val messageSupportUseCase by lazy { Creator.provideMessageSupportUseCase() }
    private val userAgreementUseCase by lazy { Creator.provideUserAgreementUseCase() }

    fun switchDarkTheme(darkModeOn: Boolean) {
        setDarkModeUseCase.execute(darkModeOn)
        App.appDarkMode = darkModeOn
        renderState(SettingsState.Default)
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

    private fun renderState(state: SettingsState) {
        stateLiveData.postValue(state)
    }
}