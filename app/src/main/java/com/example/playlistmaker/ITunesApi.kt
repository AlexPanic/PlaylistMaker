package com.example.playlistmaker

import com.example.playlistmaker.domain.models.Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
class FindTracksResponse(val results: List<Track>)
interface ItunesApi{
    @GET("/search?entity=song")
    fun findTracks(
        @Query("term") text: String
    ) : Call<FindTracksResponse>
}