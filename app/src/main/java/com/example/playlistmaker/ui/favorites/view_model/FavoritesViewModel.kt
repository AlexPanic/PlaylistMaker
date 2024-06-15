package com.example.playlistmaker.ui.favorites.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.favorites.FavoritesState
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val context: Context,
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {

    private val _data = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = _data

    fun fillData() {
        renderState(FavoritesState.Loading)
        viewModelScope.launch {
            favoritesInteractor
                .getFavorites()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty(context.getString(R.string.empty_favorites)))
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }

    private fun renderState(state: FavoritesState) {
        _data.postValue(state)
    }
}