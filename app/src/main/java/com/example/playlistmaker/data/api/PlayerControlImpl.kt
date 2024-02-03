package com.example.playlistmaker.data.api

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerControl
import com.example.playlistmaker.domain.models.PlayerFeedback
import com.example.playlistmaker.presentation.enums.PlayerState

class PlayerControlImpl : PlayerControl {
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT
    override fun prepare(url: String): PlayerFeedback.State {
        with(mediaPlayer) {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                playerState = PlayerState.PREPARED
            }
            setOnCompletionListener {
                playerState = PlayerState.PLAYBACK_COMPLETE
            }
        }
        return PlayerFeedback.State(playerState)
    }

    override fun start(): PlayerFeedback.State {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
        return PlayerFeedback.State(playerState)
    }

    override fun pause(): PlayerFeedback.State {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
        return PlayerFeedback.State(playerState)
    }

    override fun release(): PlayerFeedback.State {
        mediaPlayer.release()
        playerState = PlayerState.DEFAULT
        return PlayerFeedback.State(playerState)
    }

    override fun getState(): PlayerFeedback.State {
        return PlayerFeedback.State(playerState)
    }

    override fun getPosition(): PlayerFeedback.Position {
        return PlayerFeedback.Position(mediaPlayer.currentPosition)
    }
}