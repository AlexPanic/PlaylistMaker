package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackSearchHistory(private val sharedPref: SharedPreferences) {

    private val maxTracks = 10
    val searchHistoryKey = "SearchHistoryKey"
    fun clearTracks() {
        sharedPref.edit()
            .putString(searchHistoryKey, "")
            .apply()
    }

    fun getTracks(): MutableList<Track> {
        val strJson = sharedPref.getString(searchHistoryKey, "")
        return if (strJson!!.isNotEmpty()) {
            val type = object : TypeToken<MutableList<Track?>?>() {}.type
            return Gson().fromJson(strJson, type)
        } else {
            mutableListOf<Track>()
        }
    }

    fun addTrack(newTrack: Track) {
        var tracks = getTracks()
        if (tracks.isNotEmpty()) {
            tracks.removeIf { track -> track.trackId == newTrack.trackId }
        }
        if (tracks.size == maxTracks) {
            tracks.removeAt(maxTracks - 1)
        }
        tracks.add(0, newTrack);
        val sp = App.getSharedPreferences()
        sp.edit()
            .putString(searchHistoryKey, Gson().toJson(tracks))
            .apply()
    }

}