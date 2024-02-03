package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerFeedback

interface PlayerControl {
    fun prepare(url: String): PlayerFeedback.State
    fun start(): PlayerFeedback.State
    fun pause(): PlayerFeedback.State
    fun release(): PlayerFeedback.State
    fun getState(): PlayerFeedback.State
    fun getPosition(): PlayerFeedback.Position

}