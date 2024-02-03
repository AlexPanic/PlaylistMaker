package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.api.ControlPlayer
import com.example.playlistmaker.domain.api.PlayerConsumer
import com.example.playlistmaker.domain.api.PlayerControl
import com.example.playlistmaker.domain.models.PlayerFeedback
import com.example.playlistmaker.presentation.enums.PlayerCommand

class ControlPlayerUseCase(private val playerControl: PlayerControl) : ControlPlayer {
    override fun execute(command: PlayerCommand, consumer: PlayerConsumer, data: String?) {
        val feedback: PlayerFeedback
        feedback = when (command) {
            PlayerCommand.PREPARE -> playerControl.prepare(data!!)
            PlayerCommand.PLAY -> playerControl.start()
            PlayerCommand.PAUSE -> playerControl.pause()
            PlayerCommand.RELEASE -> playerControl.release()
            PlayerCommand.GET_POSITION -> playerControl.getPosition()
            PlayerCommand.GET_STATE -> playerControl.getState()
        }
        consumer.consume(feedback)
    }
}