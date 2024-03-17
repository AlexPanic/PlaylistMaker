package com.example.playlistmaker.domain.mediateka.impl

import com.example.playlistmaker.domain.mediateka.FavoritesInteractor
import com.example.playlistmaker.domain.mediateka.model.Favorites
import java.util.concurrent.Executors

class FavoritesInteractorImpl : FavoritesInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun getFavorites(consumer: (Favorites?, String?) -> Unit) {
        executor.execute {
            consumer.invoke(null, "Ваша медиатека пуста")
        }
    }
}