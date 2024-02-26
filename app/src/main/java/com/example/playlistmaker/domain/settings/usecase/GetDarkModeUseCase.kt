package com.example.playlistmaker.domain.settings.usecase

interface GetDarkModeUseCase {
    fun execute(darkModeOn: Boolean): Boolean
}