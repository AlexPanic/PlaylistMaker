package com.example.playlistmaker.domain.mediateka

import com.example.playlistmaker.domain.mediateka.model.Favorites

interface FavoritesInteractor {
    fun getFavorites(consumer: (Favorites?, String?) -> Unit)
}