package com.example.playlistmaker.ui.mediateka.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.mediateka.FavoritesConsumer
import com.example.playlistmaker.domain.mediateka.FavoritesInteractor
import com.example.playlistmaker.domain.mediateka.FavoritesState
import com.example.playlistmaker.domain.mediateka.model.Favorites

class FavoritesViewModel(
    favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _data = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = _data

    init {
        favoritesInteractor.getFavorites(object : FavoritesConsumer {
            override fun consume(favorites: Favorites?, errorMessage: String?) {
                _data.postValue(FavoritesState.Error(errorMessage ?: "Unknown Error"))
            }
        })
    }
}