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
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareSubject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareText))
            startActivity(Intent.createChooser(intent, getString(R.string.shareTitle)))
        }

        findViewById<FrameLayout>(R.id.action_support).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+getString(R.string.mailTo)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.supportSubject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.supportText))
            startActivity(intent)
        }

        findViewById<FrameLayout>(R.id.action_agreement).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreementUrl)))
            startActivity(intent)
        }

    }
}