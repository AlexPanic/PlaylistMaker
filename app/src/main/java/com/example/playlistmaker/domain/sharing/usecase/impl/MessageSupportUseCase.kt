package com.example.playlistmaker.domain.sharing.usecase.impl

import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.usecase.IMessageSupportUseCase

class MessageSupportUseCase(private val externalNavigator: ExternalNavigator) :
    IMessageSupportUseCase {
    override fun execute() {
        externalNavigator.messageSupport()
    }

}