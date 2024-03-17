package com.example.playlistmaker.domain.player

interface PlayerRepository {
    fun prepare(url: String): PlayerFeedback.State
    fun start(): PlayerFeedback.State
    fun pause(): PlayerFeedback.State
    fun release(): PlayerFeedback.State
    fun getState(): PlayerFeedback.State
    fun getPosition(): PlayerFeedback.Position

}