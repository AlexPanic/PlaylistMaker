package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerFeedback

interface PlayerConsumer {
    fun consume(data: PlayerFeedback)
}