package com.example.playlistmaker.data.search

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context,
) : TracksRepository {
    override fun findTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            200 -> {
                if ((response as TracksSearchResponse).results.isEmpty())
                    Resource.Error(context.getString(R.string.nothing_found))
                else
                Resource.Success(response.results.map {
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
                        country = it.country,
                    )
                })
            }

            404 -> {
                Resource.Error(context.getString(R.string.nothing_found))
            }

            else -> {
                Resource.Error(context.getString(R.string.something_went_wrong))
            }
        }
    }
}