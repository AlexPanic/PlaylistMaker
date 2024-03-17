package com.example.playlistmaker.ui.mediateka.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.mediateka.PlaylistsInteractor
import com.example.playlistmaker.domain.mediateka.PlaylistsState

class PlaylistsViewModel(
    playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _data = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = _data

    init {
        playlistsInteractor.getPlaylists { playlists, errorMessage ->
            _data.postValue(PlaylistsState.Error(errorMessage ?: "Unknown Error"))
        }
    }
}