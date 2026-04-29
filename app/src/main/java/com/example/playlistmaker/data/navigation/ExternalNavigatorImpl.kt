package com.example.playlistmaker.data.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.navigation.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {
    override fun shareApp(){
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.url_share))
        }
        context.startActivity(Intent.createChooser(intent, "Send in ..."))
    }


    override fun openSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.mail)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.theme))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.message))
        }
        context.startActivity(intent)
    }

    override fun openAgreement() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.url_yandex)))
        context.startActivity(intent)
    }
}