package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.navigation.ExternalNavigator
import com.example.playlistmaker.domain.interactors.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
): SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareApp()
    }

    override fun openSupport() {
        externalNavigator.openSupport()
    }

    override fun openAgreement() {
        externalNavigator.openAgreement()
    }
}