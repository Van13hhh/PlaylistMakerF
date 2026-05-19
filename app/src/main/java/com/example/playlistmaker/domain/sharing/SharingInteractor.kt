package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.sharing.model.SupportInfo

interface SharingInteractor{
    fun getShareUrl(): String
    fun getSupportInfo(): SupportInfo
    fun getAgreementUrl(): String
}