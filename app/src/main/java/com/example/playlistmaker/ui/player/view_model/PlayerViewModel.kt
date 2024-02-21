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
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.PlayerFeedback
import com.example.playlistmaker.domain.player.model.PlayerState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.enums.PlayerCommand

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

    // TODO получить track в Init?
    private val track: Track? = null
    private var trackIsPlaying = false
    private val playerInteractor = Creator.providePlayerInteractor(getApplication())
    private val handler = Handler(Looper.getMainLooper())
    private val stateLiveData = MutableLiveData<PlayerState>().apply {
        value = PlayerState.Default
    }

    fun observeState(): LiveData<PlayerState> = stateLiveData

    fun prepare(url: String) {
        playerInteractor.execute(
            command = PlayerCommand.PREPARE,
            consumer = object : PlayerInteractor.PlayerConsumer {
                override fun consume(feedback: PlayerFeedback) {
                    renderState(PlayerState.Prepared)
                    trackIsPlaying = false
                }
            },
            params = url
        )
    }

    fun play() {
        playerInteractor.execute(
            command = PlayerCommand.PLAY,
            consumer =  object : PlayerInteractor.PlayerConsumer {
                override fun consume(feedback: PlayerFeedback) {
                    renderState(PlayerState.Playing)
                    trackIsPlaying = true
                }
            }
        )
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

}