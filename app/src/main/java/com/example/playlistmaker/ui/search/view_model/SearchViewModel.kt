package com.example.playlistmaker.ui.search.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.SingleLiveEvent
import com.example.playlistmaker.domain.search.SearchState
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.SaveSearchHistoryUseCase

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
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private var history: MutableList<Track> = getSearchHistoryUseCase.execute().toMutableList()
    private val handler = Handler(Looper.getMainLooper())

    private val _state = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = _state

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    private var latestSearchText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String, force: Boolean) {
        if ((latestSearchText == changedText) && !force) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
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

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            tracksInteractor.findTracks(
                expression = newSearchText,
                consumer = { foundTracks, errorMessage ->

                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }
                    when (errorMessage) {
                        null -> {
                            renderState(
                                SearchState.Content(tracks = tracks)
                            )
                        }

                        context.getString(R.string.nothing_found) -> {
                            renderState(
                                SearchState.Empty(
                                    message = errorMessage
                                )
                            )
                        }

                        else -> {
                            renderState(
                                SearchState.Error(
                                    errorMessage
                                )
                            )
                        }

                    }

                }
            )
        }
    }

    private fun renderState(state: SearchState) {
        _state.postValue(state)
    }
}