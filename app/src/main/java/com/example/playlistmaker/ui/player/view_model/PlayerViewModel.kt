package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.mediateka.FavoritesInteractor
import com.example.playlistmaker.domain.player.PlayerFeedback
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.enums.PlayerCommand
import com.example.playlistmaker.ui.enums.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {
    companion object {
        private const val DEFAULT_TIMER = "00:00"
        private const val TIMER_DELAY_MILLIS = 300L
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var trackIsPlaying = false
    private var isPrepared = false
    private var isClickAllowed = true
    private var timerJob: Job? = null
    private val _state = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    private val _position = MutableLiveData<String>()
    private val _isFavorite = MutableLiveData<Boolean>(false)

    private val playerConsumer = { feedback: PlayerFeedback ->
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

    fun observeState(): LiveData<PlayerState> = _state
    fun observePosition(): LiveData<String> = _position
    fun observeIsFavorite(): LiveData<Boolean> = _isFavorite

    suspend fun toggleFavorite(isFavorite: Boolean, track: Track) {
        viewModelScope.launch {
            when (isFavorite) {
                false -> {
                    favoritesInteractor
                        .addToFavorites(track)
                        .collect {
                            _isFavorite.postValue(true)
                        }
                }

                true -> {
                    favoritesInteractor
                        .removeFromFavorites(track)
                        .collect {
                            _isFavorite.postValue(false)
                        }

                }
            }
        }
    }

    fun prepare(url: String, trackID: Int) {
        viewModelScope.launch {
            favoritesInteractor
                .isFavorite(trackID)
                .collect {
                    if (it) {
                        _isFavorite.postValue(it)
                    }
                }
        }
        if (!isPrepared) {
            playerInteractor.execute(
                command = PlayerCommand.PREPARE,
                consumer = playerConsumer,
                params = url
            )
        }
    }

    fun playPause() {
        playerInteractor.execute(command = PlayerCommand.GET_STATE, consumer = playerConsumer)
        if (clickDebounce() && isPrepared) {
            if (trackIsPlaying) {
                playerInteractor.execute(
                    command = PlayerCommand.PAUSE,
                    consumer = playerConsumer
                )
                timerJob?.cancel()
            } else {
                playerInteractor.execute(
                    command = PlayerCommand.PLAY,
                    consumer = playerConsumer
                )
                startTimer()
            }
        }
    }

    fun onActivityPause() {
        if (isPrepared) {
            playerInteractor.execute(command = PlayerCommand.PAUSE, consumer = playerConsumer)
        }
    }

    fun onActivityResume() {
        if (isPrepared) {
            playerInteractor.execute(command = PlayerCommand.PLAY, consumer = playerConsumer)
        }
    }

    override fun onCleared() {
        playerInteractor.execute(command = PlayerCommand.RELEASE, consumer = playerConsumer)
    }

    private fun updatePlayerPosition() {
        if (trackIsPlaying) {
            playerInteractor.execute(
                command = PlayerCommand.GET_POSITION,
                consumer = playerConsumer
            )
            playerInteractor.execute(command = PlayerCommand.GET_STATE, consumer = playerConsumer)
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (trackIsPlaying) {
                delay(TIMER_DELAY_MILLIS)
                updatePlayerPosition()
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                isClickAllowed = true
            }
        }
        return current
    }


}