package com.example.playlistmaker.domain.player.model

sealed interface PlayerState {
    object Default: PlayerState
    object Prepared: PlayerState
    object Playing: PlayerState
}