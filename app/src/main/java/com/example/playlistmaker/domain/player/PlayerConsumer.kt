package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.player.model.PlayerFeedback

interface PlayerConsumer {
    fun consume(feedback: PlayerFeedback)
}