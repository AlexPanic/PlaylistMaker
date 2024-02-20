package com.example.playlistmaker.domain.sharing.usecase.impl

import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.usecase.IUserAgreementUseCase

class UserAgreementUseCase(private val externalNavigator: ExternalNavigator) :
    IUserAgreementUseCase {
    override fun execute() {
        externalNavigator.userAgreement()
    }
}