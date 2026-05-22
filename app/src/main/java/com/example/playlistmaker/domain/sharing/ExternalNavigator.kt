package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.sharing.model.SupportInfo

interface ExternalNavigator {
    fun getAgreementUrl(): String
    fun getSupportInfo(): SupportInfo
    fun getShareUrl(): String
}