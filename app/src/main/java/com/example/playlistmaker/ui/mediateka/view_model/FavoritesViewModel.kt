package com.example.playlistmaker.ui.mediateka.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.mediateka.FavoritesInteractor
import com.example.playlistmaker.domain.mediateka.FavoritesState

class FavoritesViewModel(
    favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _data = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = _data

    init {
        favoritesInteractor.getFavorites { favorites, errorMessage ->
            _data.postValue(FavoritesState.Error(errorMessage ?: "Unknown Error"))
        }
    }
}