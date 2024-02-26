package com.example.playlistmaker.domain.sharing.usecase.impl

import com.example.playlistmaker.domain.settings.ExternalNavigator
import com.example.playlistmaker.domain.sharing.usecase.UserAgreementUseCase

class UserAgreementUseCaseImpl(private val externalNavigator: ExternalNavigator) :
    UserAgreementUseCase {
    override fun execute() {
        externalNavigator.userAgreement()
    }
}