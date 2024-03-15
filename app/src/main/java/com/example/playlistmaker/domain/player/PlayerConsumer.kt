package com.example.playlistmaker.domain.player

interface PlayerConsumer {
    fun consume(feedback: PlayerFeedback)
}