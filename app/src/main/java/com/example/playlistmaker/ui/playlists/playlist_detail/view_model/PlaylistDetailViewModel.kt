package com.example.playlistmaker.ui.playlists.playlist_detail.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.ExternalNavigator
import com.example.playlistmaker.domain.playlists.PlaylistDetailState
import com.example.playlistmaker.domain.playlists.PlaylistTracksState
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailViewModel(
    private val context: Context,
    private val playlistsInteractor: PlaylistsInteractor,
    private val externalNavigator: ExternalNavigator,
) : ViewModel() {

    private val dateFormat by lazy { SimpleDateFormat("m", Locale.getDefault()) }
    private var totalTimeInMillis: Int = 0
    private val _data = MutableLiveData<PlaylistDetailState>()
    private val _tracks = MutableLiveData<PlaylistTracksState>()
    fun observeState(): LiveData<PlaylistDetailState> = _data
    fun observeTracks(): LiveData<PlaylistTracksState> = _tracks

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylist(playlistId).collect { playlist ->
                loadTracks(playlist)
            }
        }
    }

    // сортируем треки в порядке добавления в плейлист (отраженном в trackIDs)
    private fun sortByAdded(tracks: List<Track>, trackIDs: List<Int>): List<Track> {
        return if (tracks.size>1) {
            val tracksA = tracks.associateBy { it.trackId }
            // в обратном порядке
            trackIDs.reversed().mapNotNull { tracksA[it] }.toList()
        } else tracks
    }

    private suspend fun loadTracks(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.getTracks(playlist.trackIDs).collect { tracks ->
                if (tracks.isEmpty()) {
                    totalTimeInMillis = 0
                    _tracks.postValue(
                        PlaylistTracksState.Empty
                    )
                } else {
                    totalTimeInMillis = tracks.sumOf { it.trackTimeMillis }
                    _tracks.postValue(
                        PlaylistTracksState.Content(sortByAdded(tracks, playlist.trackIDs))
                    )
                }
                _data.postValue(
                    PlaylistDetailState.Content(
                        playlist, dateFormat.format(totalTimeInMillis).toInt()
                    )
                )

            }
        }
    }


    fun removeTrackFromPlaylist(trackId: Int, playlistId: Long) {
        viewModelScope.launch {
            playlistsInteractor.removeTrack(trackId, playlistId).first()
            loadPlaylist(playlistId)
        }
    }

    fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {

        var message = playlist.name
        if (!playlist.description.isNullOrBlank()) {
            message += "\n${playlist.description}"
        }
        val tracksCountStr = context.getString(
            R.string.playlist_tracks_count,
            playlist.tracksCount,
            context.resources.getQuantityString(R.plurals.tracks, playlist.tracksCount)
        )
        message += "\n$tracksCountStr"
        for (track in tracks.withIndex()) {
            message += "\n${track.index + 1}. ${track.value.artistName} - ${track.value.trackName} (${track.value.trackTime()})"
        }

        externalNavigator.shareThis(context.getString(R.string.playlist_share_title), message)
    }
}