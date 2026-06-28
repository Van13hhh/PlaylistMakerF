package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.SupportInfo

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {
    override fun getShareUrl(): String {
        return externalNavigator.getShareUrl()
    }

    override fun getSupportInfo(): SupportInfo {
        return externalNavigator.getSupportInfo()
    }

    override fun getAgreementUrl(): String {
        return externalNavigator.getAgreementUrl()
    }
}