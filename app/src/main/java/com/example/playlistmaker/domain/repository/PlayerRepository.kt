package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.PlayerFeedback

interface PlayerRepository {
    fun prepare(url: String?): PlayerFeedback.State
    fun start(): PlayerFeedback.State
    fun pause(): PlayerFeedback.State
    fun release(): PlayerFeedback.State
    fun getState(): PlayerFeedback.State
    fun getPosition(): PlayerFeedback.Position

}