package com.example.playlistmaker.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context,
) : TracksRepository {
    override fun findTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {

            200 -> {
                if ((response as TracksSearchResponse).results.isEmpty())
                    emit(Resource.Error(context.getString(R.string.nothing_found)))
                else
                    emit(Resource.Success(response.results.map {
                        Track(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = it.trackTimeMillis,
                            artworkUrl100 = it.artworkUrl100.orEmpty(),
                            previewUrl = it.previewUrl.orEmpty(),
                            collectionName = it.collectionName.orEmpty(),
                            releaseDate = it.releaseDate.orEmpty(),
                            primaryGenreName = it.primaryGenreName.orEmpty(),
                            country = it.country.orEmpty(),
                        )
                    }))
            }

            404, 500 -> {
                emit(Resource.Error(context.getString(R.string.nothing_found)))
            }

            else -> {
                emit(Resource.Error(context.getString(R.string.something_went_wrong)))
            }
        }
    }
}