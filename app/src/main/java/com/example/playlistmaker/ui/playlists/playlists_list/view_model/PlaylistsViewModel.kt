package com.example.playlistmaker.ui.playlists.playlists_list.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsState
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val context: Context,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {
    private val _data = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = _data

    fun fillData() {
        renderState(PlaylistsState.Loading)
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty(context.getString(R.string.empty_playlists)))
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistsState) {
        _data.postValue(state)
    }
}