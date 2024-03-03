package com.example.playlistmaker.ui.main.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.media.activity.MediaActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.btnSearch?.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        binding?.btnSettings?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding?.btnMedia?.setOnClickListener{
            startActivity(Intent(this, MediaActivity::class.java))
        }

    }
}