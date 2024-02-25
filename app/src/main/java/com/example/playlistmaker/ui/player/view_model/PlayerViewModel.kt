package com.example.playlistmaker.ui.player.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.player.PlayerConsumer
import com.example.playlistmaker.domain.player.model.PlayerFeedback
import com.example.playlistmaker.ui.enums.PlayerCommand
import com.example.playlistmaker.ui.enums.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val DEFAULT_TIMER = "00:00"
        private const val DELAY_MILLIS = 250L
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private val PLAYER_POSITION_TOKEN = Any()
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var trackIsPlaying = false
    private var isPrepared = false
    private var isClickAllowed = true
    private val playerInteractor = Creator.providePlayerInteractor()
    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable { updatePlayerPosition() }

    private val _state = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    private val _position = MutableLiveData<String>()

    private val playerConsumer = object : PlayerConsumer {
        override fun consume(feedback: PlayerFeedback) {
            when (feedback) {
                is PlayerFeedback.State -> {
                    _state.postValue(feedback.state)
                    trackIsPlaying = feedback.state == PlayerState.PLAYING
                    when (feedback.state) {
                        PlayerState.PREPARED -> {
                            isPrepared = true
                        }

                        PlayerState.DEFAULT,
                        PlayerState.PLAYBACK_COMPLETE -> {
                            _position.postValue(DEFAULT_TIMER)
                        }

                        else -> {}
                    }
                }

                is PlayerFeedback.Position -> {
                    _position.postValue(dateFormat.format(feedback.playPositionMillis))
                }
            }
        }
    }

    fun observeState(): LiveData<PlayerState> = _state
    fun observePosition(): LiveData<String> = _position

    fun prepare(url: String) {
        playerInteractor.execute(
            command = PlayerCommand.PREPARE,
            consumer = playerConsumer,
            params = url
        )
    }

    fun playPause() {
        playerInteractor.execute(command = PlayerCommand.GET_STATE, consumer = playerConsumer)
        if (clickDebounce() && isPrepared) {
            if (trackIsPlaying) {
                playerInteractor.execute(
                    command = PlayerCommand.PAUSE,
                    consumer = playerConsumer
                )
            } else {
                playerInteractor.execute(
                    command = PlayerCommand.PLAY,
                    consumer = playerConsumer
                )
                handler.post(runnable)
            }
        }
    }

    fun onActivityPause() {
        if (isPrepared) {
            playerInteractor.execute(command = PlayerCommand.PAUSE, consumer = playerConsumer)
        }
        handler.removeCallbacksAndMessages(PLAYER_POSITION_TOKEN)
    }

    fun onActivityResume() {
        if (isPrepared) {
            playerInteractor.execute(command = PlayerCommand.PLAY, consumer = playerConsumer)
        }
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(PLAYER_POSITION_TOKEN)
        playerInteractor.execute(command = PlayerCommand.RELEASE, consumer = playerConsumer)
    }

    private fun updatePlayerPosition() {
        if (trackIsPlaying) {
            playerInteractor.execute(
                command = PlayerCommand.GET_POSITION,
                consumer = playerConsumer
            )
            playerInteractor.execute(command = PlayerCommand.GET_STATE, consumer = playerConsumer)
            handler.postDelayed(
                runnable,
                PLAYER_POSITION_TOKEN,
                DELAY_MILLIS
            )
        }
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


}