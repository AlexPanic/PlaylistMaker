package com.example.playlistmaker.presentation.ui.player

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.domain.api.PlayerConsumer
import com.example.playlistmaker.domain.models.PlayerFeedback
import com.example.playlistmaker.presentation.enums.PlayerCommand
import com.example.playlistmaker.presentation.enums.PlayerState

class PlayerInteract(val playerUiUpdater: PlayerUiUpdater) {

    private var isClickAllowed = true
    private val player by lazy { Creator.provideControlPlayerUseCase() }
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { refreshPlayerData() }
    private val uiUpdater = object : PlayerConsumer {
        override fun consume(data: PlayerFeedback) {
            when (data) {
                is PlayerFeedback.State -> onPlayerStateChange(data.state)
                is PlayerFeedback.Position -> playerUiUpdater.onPositionChange(data.playPositionMillis)
            }
        }
    }

    fun init(url: String) {
        player.execute(PlayerCommand.PREPARE, uiUpdater, url)
        //Log.d("mine", "SEND PREPARE..")
    }

    fun playPauseToggle(trackIsPlaying: Boolean) {
        if (clickDebounce()) {
            if (trackIsPlaying) {
                player.execute(PlayerCommand.PAUSE, uiUpdater)
            } else {
                player.execute(PlayerCommand.PLAY, uiUpdater)
                handler.post(runnable)
            }
        }
    }

    fun onActivityDestroy() {
        player.execute(PlayerCommand.RELEASE, uiUpdater)
    }

    fun onActivityPause() {
        player.execute(PlayerCommand.PAUSE, uiUpdater)
        handler.removeCallbacksAndMessages(runnable)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(
                { isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY_MILLIS
            )
        }
        return current
    }

    private fun refreshPlayerData() {
        player.execute(PlayerCommand.GET_POSITION, uiUpdater)
        player.execute(PlayerCommand.GET_STATE, uiUpdater)
        if (playerUiUpdater.isTrackPlaying()) handler.postDelayed(runnable, DELAY_MILLIS)
    }

    private fun onPlayerStateChange(state: PlayerState) {
        //Log.d("mine", "STATE = " + PlayerFeedback.State(state))
        when (state) {
            PlayerState.DEFAULT -> playerUiUpdater.onPlayerDefault()
            PlayerState.PREPARED -> playerUiUpdater.onPlayerPrepared()
            PlayerState.PLAYING -> playerUiUpdater.onPlayerPlaying()
            PlayerState.PAUSED -> playerUiUpdater.onPlayerPaused()
            PlayerState.PLAYBACK_COMPLETE -> playerUiUpdater.onPlayerPlaybackCompleted()
        }
    }

    companion object {
        private const val DELAY_MILLIS = 250L
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}