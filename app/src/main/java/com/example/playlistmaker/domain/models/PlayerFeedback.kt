package com.example.playlistmaker.domain.models

import com.example.playlistmaker.presentation.enums.PlayerState

sealed interface PlayerFeedback {
    data class State(val state: PlayerState) : PlayerFeedback
    data class Position(val playPositionMillis: Int) : PlayerFeedback
}