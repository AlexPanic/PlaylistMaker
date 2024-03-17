package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun findTracks(expression: String, consumer: (List<Track>?, String?) -> Unit) {
        executor.execute {
            when (val resource = repository.findTracks(expression)) {
                is Resource.Success -> {
                    consumer.invoke(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.invoke(null, resource.message)
                }
            }
        }
    }
}