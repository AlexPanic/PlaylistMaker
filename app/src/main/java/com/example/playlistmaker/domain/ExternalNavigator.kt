package com.example.playlistmaker.domain

interface ExternalNavigator {
    fun shareThis(subj: String, message: String)
    fun messageSupport()
    fun userAgreement()
}