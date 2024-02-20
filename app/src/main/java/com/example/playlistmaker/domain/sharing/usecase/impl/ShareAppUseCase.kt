package com.example.playlistmaker.domain.sharing.usecase.impl

import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.usecase.IShareAppUseCase

class ShareAppUseCase(private val externalNavigator: ExternalNavigator) : IShareAppUseCase {
    override fun execute() {
        externalNavigator.shareApp()
    }

}