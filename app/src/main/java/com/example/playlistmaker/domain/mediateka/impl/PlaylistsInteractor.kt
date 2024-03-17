package com.example.playlistmaker.domain.mediateka.impl

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.mediateka.PlaylistsInteractor
import com.example.playlistmaker.domain.mediateka.model.Playlist
import java.util.concurrent.Executors

class PlaylistsInteractorImpl(private val context: Context) : PlaylistsInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun getPlaylists(consumer: (List<Playlist>?, String?) -> Unit) {
        executor.execute {

            consumer.invoke(null, ContextCompat.getString(context, R.string.empty_playlists))
        }
    }
    override fun getPlaylistTracks() {
    }
}