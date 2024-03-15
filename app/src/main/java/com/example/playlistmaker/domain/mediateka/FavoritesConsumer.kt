package com.example.playlistmaker.domain.mediateka

import com.example.playlistmaker.domain.mediateka.model.Favorites

interface FavoritesConsumer {
    fun consume(favorites: Favorites?, errorMessage: String?)
}