package com.example.playlistmaker.domain.sharing.usecase.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.ExternalNavigator
import com.example.playlistmaker.domain.sharing.usecase.ShareAppUseCase

class ShareAppUseCaseImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator,
) : ShareAppUseCase {
    override fun execute() {
        externalNavigator.shareThis(context.getString(R.string.share_subject), context.getString(R.string.share_text))
    }

}