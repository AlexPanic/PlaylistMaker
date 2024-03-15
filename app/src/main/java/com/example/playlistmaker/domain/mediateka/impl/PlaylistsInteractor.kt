package com.example.playlistmaker.domain.mediateka.impl

import com.example.playlistmaker.domain.mediateka.PlaylistsConsumer
import com.example.playlistmaker.domain.mediateka.PlaylistsInteractor
import java.util.concurrent.Executors

class PlaylistsInteractorImpl : PlaylistsInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun getPlaylists(consumer: PlaylistsConsumer) {
        executor.execute {
            consumer.consume(null, "Вы не создали\nни одного плейлиста")
        }
    }

    override fun getPlaylistTracks() {
    }
}