package com.example.playlistmaker.domain.repository

import android.content.Intent
import com.example.playlistmaker.domain.models.Track

interface TheTrackRepository {
    fun getTrack(intent: Intent): Track
}