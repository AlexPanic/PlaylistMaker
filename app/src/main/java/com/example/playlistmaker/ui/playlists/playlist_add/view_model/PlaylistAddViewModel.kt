package com.example.playlistmaker.ui.playlists.playlist_add.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistSubmitState
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PlaylistAddViewModel(
    private val context: Context,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {
    private val _data = MutableLiveData<PlaylistSubmitState>()
    fun observeState(): LiveData<PlaylistSubmitState> = _data

    fun updatePlaylist(
        playlist: Playlist,
        playlistName: String,
        playlistDescription: String?,
        newUri: Uri?,
    ) {
        playlist.name = playlistName
        playlist.description = playlistDescription
        if (newUri!=null) {
            playlist.cover = saveImageToPrivateStorage(newUri, playlist.id)
        }
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(playlist)
                .collect{
                    renderState(PlaylistSubmitState.Updated)
                }
        }
    }

    fun addPlaylist(
        playlistName: String,
        playlistDescription: String?,
        uri: Uri?,
    ) {
        renderState(PlaylistSubmitState.Loading)
        viewModelScope.launch {
            val playlist = Playlist(
                id = 0,
                name = playlistName,
                description = playlistDescription,
                tracksCount = 0,
            )
            playlistsInteractor
                .addPlaylist(playlist)
                .collect { playlistID ->
                    if (uri != null) {
                        val savedCover = saveImageToPrivateStorage(uri, playlistID)
                        playlistsInteractor
                            .updateCover(savedCover, playlistID)
                            .collect {
                                renderState(PlaylistSubmitState.Added)
                            }
                    } else {
                        renderState(PlaylistSubmitState.Added)
                    }
                }
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, playlistId: Long): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), ALBUM)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${System.currentTimeMillis()}$playlistId")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return file.toURI().toString()
    }


/*
    fun submitPlaylist(
        playlistId: Long = 0,
        playlistName: String,
        playlistDescription: String?,
        uri: Uri?,
    ) {
        renderState(PlaylistSubmitState.Loading)
        val playlist = Playlist(
            id = playlistId,
            name = playlistName,
            description = playlistDescription,
            tracksCount = 0,
            cover = uri.toString()
        )

        Log.d("mine", playlist.toString())


        if (playlistId > 0) {
            //updatePlaylist(playlist)
        } else {
            addPlaylist(playlist)
        }

    }

    private fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor
                .addPlaylist(playlist)
                .collect { playlistID ->
                    if (playlist.cover != null) {
                        val savedCover =
                            saveImageToPrivateStorage(playlist.cover.toUri(), playlistID)
                        playlistsInteractor
                            .updateCover(savedCover, playlistID)
                            .collect {
                                renderState(PlaylistSubmitState.Added)
                            }
                    } else {
                        renderState(PlaylistSubmitState.Added)
                    }
                }
        }
    }

    private fun updatePlaylist(playlist: Playlist) {
        renderState(PlaylistSubmitState.Loading)
        viewModelScope.launch {
            playlistsInteractor
                .updatePlaylist(playlist)
                .collect {
                    if (playlist.cover != null) {
                        val savedCover =
                            saveImageToPrivateStorage(playlist.cover.toUri(), playlist.id)
                        playlistsInteractor
                            .updateCover(savedCover, playlist.id)
                            .collect {
                                renderState(PlaylistSubmitState.Updated)
                            }

                    } else {
                        renderState(PlaylistSubmitState.Updated)
                    }
                }
        }
    }

*/

    private fun renderState(state: PlaylistSubmitState) {
        _data.postValue(state)
    }

    companion object {
        const val ALBUM = "playlists"
    }
}