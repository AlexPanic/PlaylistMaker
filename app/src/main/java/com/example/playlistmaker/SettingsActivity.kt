package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<ImageView>(R.id.go_back).setOnClickListener{
            this.onBackPressed()
        }

        findViewById<Switch>(R.id.darkThemeSwitch).setOnCheckedChangeListener{ _, isChecked ->
            val editor:SharedPreferences.Editor
            val sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor = sharedPreferences.edit()
                editor.putBoolean("nightMode", true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor = sharedPreferences.edit()
                editor.putBoolean("nightMode", false)
            }
            editor.apply()
        }

        findViewById<FrameLayout>(R.id.action_share).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
        }

        findViewById<FrameLayout>(R.id.action_support).setOnClickListener {

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_to)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))
            }
            startActivity(intent)
        }

        findViewById<FrameLayout>(R.id.action_agreement).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement_url)))
            startActivity(intent)
        }

    }
}