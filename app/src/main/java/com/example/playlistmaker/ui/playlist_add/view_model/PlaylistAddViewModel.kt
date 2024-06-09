package com.example.playlistmaker.ui.playlist_add.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.playlists.PlaylistAddState
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PlaylistAddViewModel(
    private val context: Context,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {
    private val _data = MutableLiveData<PlaylistAddState>()
    fun observeState(): LiveData<PlaylistAddState> = _data
    fun addPlaylist(
        playlistName: String,
        playlistDescription: String?,
        uri: Uri?,
    ) {
        renderState(PlaylistAddState.Loading)
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
                    when (playlistID) {
                        null -> renderState(PlaylistAddState.Error(context.getString(R.string.playlist_add_error)))
                        else -> {
                            if (uri != null) {

                                Log.d("mine", "URI = $uri")
                                val savedCover = saveImageToPrivateStorage(uri, playlistID)
                                Log.d("mine", "savedCover = $savedCover")
                                playlistsInteractor
                                    .updateCover(savedCover, playlistID)
                                    .collect {
                                        Log.d("mine", "collected")
                                        renderState(PlaylistAddState.Added)
                                    }
                            }
                        }
                    }
                }
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, playlistId: Long): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), ALBUM)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "plcover$playlistId")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return file.toURI().toString()
    }

    private fun renderState(state: PlaylistAddState) {
        _data.postValue(state)
    }

    companion object {
        const val ALBUM = "playlists"
    }
}