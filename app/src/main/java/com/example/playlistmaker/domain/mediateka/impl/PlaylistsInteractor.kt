package com.example.playlistmaker.domain.mediateka.impl

import com.example.playlistmaker.domain.mediateka.PlaylistsInteractor
import com.example.playlistmaker.domain.mediateka.model.Playlist
import java.util.concurrent.Executors

class PlaylistsInteractorImpl : PlaylistsInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun getPlaylists(consumer: (List<Playlist>?, String?) -> Unit) {
        executor.execute {
            consumer.invoke(null, "Вы не создали\nни одного плейлиста")
        }
    }
    override fun getPlaylistTracks() {
    }
}