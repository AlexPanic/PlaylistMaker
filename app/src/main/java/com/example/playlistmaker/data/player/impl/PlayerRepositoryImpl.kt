package com.example.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.PlayerRepository
import com.example.playlistmaker.domain.player.model.PlayerFeedback
import com.example.playlistmaker.ui.enums.PlayerState_

class PlayerRepositoryImpl : PlayerRepository {
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState_.DEFAULT
    override fun prepare(url: String?): PlayerFeedback.State {
        with(mediaPlayer) {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                playerState = PlayerState_.PREPARED
            }
            setOnCompletionListener {
                playerState = PlayerState_.PLAYBACK_COMPLETE
            }
        }
        return PlayerFeedback.State(playerState)
    }

    override fun start(): PlayerFeedback.State {
        mediaPlayer.start()
        playerState = PlayerState_.PLAYING
        return PlayerFeedback.State(playerState)
    }

    override fun pause(): PlayerFeedback.State {
        mediaPlayer.pause()
        playerState = PlayerState_.PAUSED
        return PlayerFeedback.State(playerState)
    }

    override fun release(): PlayerFeedback.State {
        mediaPlayer.release()
        playerState = PlayerState_.DEFAULT
        return PlayerFeedback.State(playerState)
    }

    override fun getState(): PlayerFeedback.State {
        return PlayerFeedback.State(playerState)
    }

    override fun getPosition(): PlayerFeedback.Position {
        return PlayerFeedback.Position(mediaPlayer.currentPosition)
    }
}