package com.example.playlistmaker.domain.player.usecase.impl

import com.example.playlistmaker.domain.player.PlayerFeedback
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.ui.enums.PlayerCommand

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {
    override fun execute(
        command: PlayerCommand,
        consumer: (PlayerFeedback) -> Unit,
        params: String?
    ) {
        val feedback: PlayerFeedback = when (command) {
            PlayerCommand.PREPARE -> {
                repository.prepare(params!!)
            }

            PlayerCommand.PLAY -> {
                repository.start()
            }

            PlayerCommand.PAUSE -> repository.pause()
            PlayerCommand.RELEASE -> repository.release()
            PlayerCommand.GET_POSITION -> repository.getPosition()
            PlayerCommand.GET_STATE -> repository.getState()
        }
        consumer.invoke(feedback)
    }
}