package com.example.playlistmaker.domain.api

import com.example.playlistmaker.presentation.enums.PlayerCommand

interface ControlPlayer {
    fun execute(
        command: PlayerCommand,
        consumer: PlayerConsumer,
        data: String? = null
    )
}