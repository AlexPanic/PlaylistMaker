package com.example.playlistmaker.ui.playlists.playlist_detail.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistDetailState
import com.example.playlistmaker.domain.playlists.PlaylistTracksState
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailViewModel(
    private val context: Context,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val dateFormat by lazy { SimpleDateFormat("m", Locale.getDefault()) }

    private val _data = MutableLiveData<PlaylistDetailState>()
    private val _tracks = MutableLiveData<PlaylistTracksState>()
    fun observeState(): LiveData<PlaylistDetailState> = _data
    fun observeTracks(): LiveData<PlaylistTracksState> = _tracks

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylist(playlistId)
                .collect { playlist ->
                    if (playlist.trackIDs.isEmpty()) {
                        _data.postValue(
                            PlaylistDetailState.Content(
                                playlist,
                                0
                            )
                        )
                    } else {

                        playlistsInteractor.getTracks(playlist.trackIDs)
                            .collect { tracks ->
                                _tracks.postValue(
                                    PlaylistTracksState.Content(tracks)
                                )

                                playlistsInteractor.getTrackTimeMillisTotal(playlist.trackIDs)
                                    .collect { timeTotal ->
                                        _data.postValue(
                                            PlaylistDetailState.Content(
                                                playlist,
                                                dateFormat.format(timeTotal).toInt()
                                            )
                                        )

                                    }
                            }


                    }

                }
        }

    }

    fun removeTrackFromPlaylist(trackId: Int, playlistId: Long) {
        viewModelScope.launch {
            playlistsInteractor
                .removeTrack(trackId, playlistId).first()
            loadPlaylist(playlistId)
        }
    }
}