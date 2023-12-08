package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackSearchHistory(private val sharedPref: SharedPreferences) {

    companion object {
        val sharedPrefKey = "SearchHistoryKey"
    }

    private val maxTracks = 10
    fun clearTracks() {
        sharedPref.edit()
            .putString(sharedPrefKey, "")
            .apply()
    }

    fun getTracks(): MutableList<Track> {
        val strJson = sharedPref.getString(sharedPrefKey, "")
        return if (strJson!!.isNotEmpty()) {
            val type = object : TypeToken<MutableList<Track?>?>() {}.type
            return Gson().fromJson(strJson, type)
        } else {
            mutableListOf<Track>()
        }
    }

    fun addTrack(newTrack: Track) {
        val tracks = getTracks()
        if (tracks.isNotEmpty()) {
            tracks.removeIf { track -> track.trackId == newTrack.trackId }
        }
        if (tracks.size == maxTracks) {
            tracks.removeAt(maxTracks - 1)
        }
        tracks.add(0, newTrack)
        sharedPref.edit()
            .putString(sharedPrefKey, Gson().toJson(tracks))
            .apply()
    }

}