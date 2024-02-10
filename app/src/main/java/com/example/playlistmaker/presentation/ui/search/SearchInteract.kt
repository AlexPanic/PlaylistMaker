package com.example.playlistmaker.presentation.ui.search

import android.os.Handler
import android.os.Looper

class SearchInteract(val searchUiUpdater: SearchUiUpdater) {
    private val handler = Handler(Looper.getMainLooper())

    fun clearSearchMask() {
        searchUiUpdater.onClearSearchMask()
    }
}