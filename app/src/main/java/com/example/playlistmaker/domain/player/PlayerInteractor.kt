package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.player.model.PlayerFeedback
import com.example.playlistmaker.ui.enums.PlayerCommand

interface PlayerInteractor {
    fun execute(command: PlayerCommand, consumer: PlayerConsumer, params: String?="")
    interface PlayerConsumer {
        fun consume(feedback: PlayerFeedback)
    }
}