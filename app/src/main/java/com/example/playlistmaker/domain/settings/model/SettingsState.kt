package com.example.playlistmaker.domain.settings.model

sealed interface SettingsState {
    object Default: SettingsState
}