package com.example.playlistmaker.domain.repository

import android.content.Intent
import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun findTracks(expression: String): List<Track>
}