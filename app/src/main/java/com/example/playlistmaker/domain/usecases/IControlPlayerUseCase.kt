package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.api.PlayerConsumer
import com.example.playlistmaker.presentation.enums.PlayerCommand

interface IControlPlayerUseCase {
    fun execute(
        command: PlayerCommand,
        consumer: PlayerConsumer,
        data: String? = null
    )
}