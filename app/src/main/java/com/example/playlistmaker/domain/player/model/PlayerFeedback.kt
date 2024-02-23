package com.example.playlistmaker.domain.player.model

import com.example.playlistmaker.ui.enums.PlayerState

sealed interface PlayerFeedback {
    data class State(val state: PlayerState) : PlayerFeedback
    data class Position(val playPositionMillis: Int) : PlayerFeedback
}