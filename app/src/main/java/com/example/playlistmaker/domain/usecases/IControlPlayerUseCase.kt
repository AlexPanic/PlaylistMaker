package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.player.model.PlayerConsumer
import com.example.playlistmaker.ui.enums.PlayerCommand

interface IControlPlayerUseCase {
    fun execute(
        command: PlayerCommand,
        consumer: PlayerConsumer,
        data: String? = null
    )
}