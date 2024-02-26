package com.example.playlistmaker.domain.player

import com.example.playlistmaker.ui.enums.PlayerCommand

interface PlayerInteractor {
    fun execute(command: PlayerCommand, consumer: PlayerConsumer, params: String? = "")
}