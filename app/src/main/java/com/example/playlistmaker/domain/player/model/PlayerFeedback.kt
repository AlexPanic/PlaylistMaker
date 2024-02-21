package com.example.playlistmaker.domain.player.model

import com.example.playlistmaker.ui.enums.PlayerState_

sealed interface PlayerFeedback {
    data class State(val state: PlayerState_) : PlayerFeedback
    data class Position(val playPositionMillis: Int) : PlayerFeedback
}