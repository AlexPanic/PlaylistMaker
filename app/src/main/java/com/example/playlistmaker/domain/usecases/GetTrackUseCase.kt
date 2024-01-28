package com.example.playlistmaker.domain.usecases

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TheTrackRepository

class GetTrackUseCase(
    private val repo: TheTrackRepository,
    private val intent: Intent
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun execute(): Track {
        return repo.getTrack(intent)
    }

}