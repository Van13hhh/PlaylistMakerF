package com.example.playlistmaker.data.sharing.impl

import android.app.Application
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.SupportInfo

class ExternalNavigatorImpl(private val context: Application): ExternalNavigator {

    override fun getAgreementUrl(): String {
        return  context.getString(R.string.url_yandex)
    }

    override fun getSupportInfo(): SupportInfo {
        return SupportInfo(
            context.getString(R.string.mail),
            context.getString(R.string.theme),
            context.getString(R.string.message)
        )
    }

    override fun getShareUrl(): String {
        return context.getString(R.string.url_share)
    }

}