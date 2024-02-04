package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.api.ControlPlayer
import com.example.playlistmaker.domain.api.PlayerConsumer
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.models.PlayerFeedback
import com.example.playlistmaker.presentation.enums.PlayerCommand

class ControlPlayerUseCase(private val playerRepository: PlayerRepository) : ControlPlayer {
    override fun execute(command: PlayerCommand, consumer: PlayerConsumer, data: String?) {
        val feedback: PlayerFeedback = when (command) {
            PlayerCommand.PREPARE -> playerRepository.prepare(data)
            PlayerCommand.PLAY -> playerRepository.start()
            PlayerCommand.PAUSE -> playerRepository.pause()
            PlayerCommand.RELEASE -> playerRepository.release()
            PlayerCommand.GET_POSITION -> playerRepository.getPosition()
            PlayerCommand.GET_STATE -> playerRepository.getState()
        }
        consumer.consume(feedback)
    }
}