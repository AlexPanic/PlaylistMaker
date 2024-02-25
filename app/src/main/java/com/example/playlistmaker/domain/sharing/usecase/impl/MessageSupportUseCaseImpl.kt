package com.example.playlistmaker.domain.sharing.usecase.impl

import com.example.playlistmaker.domain.settings.ExternalNavigator
import com.example.playlistmaker.domain.sharing.usecase.MessageSupportUseCase

class MessageSupportUseCaseImpl(private val externalNavigator: ExternalNavigator) :
    MessageSupportUseCase {
    override fun execute() {
        externalNavigator.messageSupport()
    }

}