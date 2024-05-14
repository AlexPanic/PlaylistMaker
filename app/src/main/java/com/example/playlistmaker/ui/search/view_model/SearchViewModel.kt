package com.example.playlistmaker.ui.search.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.SingleLiveEvent
import com.example.playlistmaker.domain.search.SearchState
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.SaveSearchHistoryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val context: Context,
    getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val saveSearchHistoryUseCase: SaveSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val HISTORY_MAX_SIZE = 10
    }

    private var history: MutableList<Track> = getSearchHistoryUseCase.execute().toMutableList()
    private val _state = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = _state

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    fun searchDebounce(changedText: String, force: Boolean) {
        if ((latestSearchText == changedText) && !force) {
            return
        }
        latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    private fun processResult(found: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (found != null) {
            tracks.addAll(found)
        }
        when (errorMessage) {
            null -> {
                renderState(
                    SearchState.Content(tracks)
                )
            }

            context.getString(R.string.nothing_found) -> {
                renderState(
                    SearchState.Empty(errorMessage)
                )
            }

            else -> {
                renderState(
                    SearchState.Error(errorMessage)
                )
            }

        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            viewModelScope.launch {
                tracksInteractor.findTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    // добавим трек в историю
    fun addToHistory(track: Track) {
        history.remove(track)
        history.add(0, track)
        if (history.size > HISTORY_MAX_SIZE) {
            history.removeAt(HISTORY_MAX_SIZE)
        }
    }

    fun showHistory() {
        renderState(SearchState.HistoryList(history))
    }

    fun saveHistory() {
        saveSearchHistoryUseCase.execute(history)
    }

    fun clearHistory() {
        clearSearchHistoryUseCase.execute()
        history.clear()
        renderState(SearchState.HistoryClear)
    }


    private fun renderState(state: SearchState) {
        _state.postValue(state)
    }
}