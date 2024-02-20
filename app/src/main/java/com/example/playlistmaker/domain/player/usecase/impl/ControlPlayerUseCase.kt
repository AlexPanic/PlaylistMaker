package com.example.playlistmaker.domain.player.usecase.impl

import com.example.playlistmaker.domain.player.model.PlayerConsumer
import com.example.playlistmaker.data.player.PlayerRepository
import com.example.playlistmaker.domain.player.model.PlayerFeedback
import com.example.playlistmaker.domain.player.usecase.IControlPlayerUseCase
import com.example.playlistmaker.ui.enums.PlayerCommand

class ControlPlayerUseCase(private val playerRepository: PlayerRepository) : IControlPlayerUseCase {
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