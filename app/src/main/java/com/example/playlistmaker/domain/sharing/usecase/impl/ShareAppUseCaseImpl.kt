package com.example.playlistmaker.domain.sharing.usecase.impl

import com.example.playlistmaker.domain.settings.ExternalNavigator
import com.example.playlistmaker.domain.sharing.usecase.ShareAppUseCase

class ShareAppUseCaseImpl(private val externalNavigator: ExternalNavigator) : ShareAppUseCase {
    override fun execute() {
        externalNavigator.shareApp()
    }

}