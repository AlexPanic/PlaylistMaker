package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.player.PlayerFeedback
import com.example.playlistmaker.ui.enums.PlayerState

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer,
) : PlayerRepository {

    private var playerState = PlayerState.DEFAULT
    override fun prepare(url: String): PlayerFeedback.State {
        with(mediaPlayer) {
            try {
                reset()
                setDataSource(url)
                prepareAsync()
            } catch (error: Throwable) {
                playerState = PlayerState.ERROR
                Log.e("player prepare error", error.toString(), error)
            }
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