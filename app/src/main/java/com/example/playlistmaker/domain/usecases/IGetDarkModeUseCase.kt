package com.example.playlistmaker.domain.usecases

interface IGetDarkModeUseCase {
    fun execute(darkModeOn: Boolean): Boolean
}