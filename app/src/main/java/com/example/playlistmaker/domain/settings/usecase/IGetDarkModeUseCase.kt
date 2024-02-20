package com.example.playlistmaker.domain.settings.usecase

interface IGetDarkModeUseCase {
    fun execute(darkModeOn: Boolean): Boolean
}