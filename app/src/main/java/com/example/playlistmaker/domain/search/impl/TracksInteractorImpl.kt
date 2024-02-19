package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.creator.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun findTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.findTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }
}