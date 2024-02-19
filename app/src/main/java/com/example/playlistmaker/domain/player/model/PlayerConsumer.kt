package com.example.playlistmaker.domain.player.model

interface PlayerConsumer {
    fun consume(data: PlayerFeedback)
}