package com.example.playlistmaker.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.getString
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.ExternalNavigator


class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareThis(subj: String, message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subj)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startIntent(intent, getString(context, R.string.share_title))
    }

    override fun messageSupport() {
        val intent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(context, R.string.mail_to)))
            putExtra(Intent.EXTRA_SUBJECT, getString(context, R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, getString(context, R.string.support_text))
        }
        startIntent(intent, "")
    }

    override fun userAgreement() {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(context, R.string.agreement_url)))
        startIntent(intent, "")
    }

    // запускаем через другой интент иначе ошибка
    // Calling startActivity() from outside of an Activity context
    private fun startIntent(intent: Intent, title: String?) {
        val chooserIntent = Intent.createChooser(intent, title ?: "")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}