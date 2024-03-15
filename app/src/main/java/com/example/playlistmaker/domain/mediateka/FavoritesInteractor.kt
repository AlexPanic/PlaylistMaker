package com.example.playlistmaker.domain.mediateka

interface FavoritesInteractor {
    fun getFavorites(consumer: FavoritesConsumer)
}