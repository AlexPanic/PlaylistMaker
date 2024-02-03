package com.example.playlistmaker.presentation

interface PlayerUiUpdater {
    fun onPlayerDefault()
    fun onPlayerPrepared()
    fun onPlayerPlaying()
    fun onPlayerPaused()
    fun onPlayerPlaybackCompleted()
    fun onPositionChange(playPositionMillis: Int)
    fun isTrackPlaying(): Boolean
}