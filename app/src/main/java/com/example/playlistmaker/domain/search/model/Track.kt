package com.example.playlistmaker.domain.search.model

import java.io.Serializable

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val previewUrl: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    var country: String
) : Serializable {
    fun getArtworkUrl512() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    fun getYear() = if (releaseDate.isNotEmpty()) {releaseDate.substring(0, 4)} else ""
    fun trackTime(): String {
        val sec = trackTimeMillis / 1000
        val mm = sec / 60
        val ss = "%02d".format(sec % 60)
        return "$mm:$ss"
    }

    companion object {
        const val INTENT_EXTRA_ID = "track_object"
    }
}