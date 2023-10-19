package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val btnBack = findViewById<ImageView>(R.id.go_back)
        btnBack.setOnClickListener{
            this.onBackPressed()
        }
    }
}