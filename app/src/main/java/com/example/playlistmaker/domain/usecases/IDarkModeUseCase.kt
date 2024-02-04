package com.example.playlistmaker.domain.usecases

interface IDarkModeUseCase {
    fun execute(darkModeOn: Boolean): Boolean
}