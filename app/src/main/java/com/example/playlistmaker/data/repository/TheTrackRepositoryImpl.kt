package com.example.playlistmaker.data.repository

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TheTrackRepository

class TheTrackRepositoryImpl: TheTrackRepository {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun getTrack(intent: Intent): Track {
        return intent.getSerializableExtra(Track.INTENT_EXTRA_ID, Track::class.java) as Track
    }
}