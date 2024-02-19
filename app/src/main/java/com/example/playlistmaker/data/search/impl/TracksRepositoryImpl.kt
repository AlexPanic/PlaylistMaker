package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.creator.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun findTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        previewUrl = it.previewUrl,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}