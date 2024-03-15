package com.example.playlistmaker.domain.mediateka.impl

import com.example.playlistmaker.domain.mediateka.FavoritesConsumer
import com.example.playlistmaker.domain.mediateka.FavoritesInteractor
import java.util.concurrent.Executors

class FavoritesInteractorImpl: FavoritesInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun getFavorites(consumer: FavoritesConsumer) {
        executor.execute{
            consumer.consume(null, "Ваша медиатека пуста")
        }
    }
}