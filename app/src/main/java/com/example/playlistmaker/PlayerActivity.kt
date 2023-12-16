package com.example.playlistmaker

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val trackJson = intent.getStringExtra("track")
        val track = Gson().fromJson(trackJson, Track::class.java)

        // увеличенная обложка
        val trackCover = findViewById<ImageView>(R.id.trackCover)
        val coverUrl = track.getArtworkUrl512()
        Glide.with(applicationContext)
            .load(coverUrl)
            .fitCenter()
            .into(trackCover)

        // свойства трека
        val trackName = findViewById<TextView>(R.id.trackName)
        trackName.text = track.trackName
        val artistName = findViewById<TextView>(R.id.artistName)
        artistName.text = track.artistName
        val trackTime = findViewById<TextView>(R.id.trackTime)
        trackTime.text = track.trackTime()
        val trackYear = findViewById<TextView>(R.id.trackYear)
        trackYear.text = track.getYear()
        val trackGenre = findViewById<TextView>(R.id.trackGenre)
        trackGenre.text = track.primaryGenreName
        val trackCountry = findViewById<TextView>(R.id.trackCountry)
        trackCountry.text = track.country

        // альбом, если у трека он есть
        val labelTrackAlbum = findViewById<TextView>(R.id.labelTrackAlbum)
        val trackAlbum = findViewById<TextView>(R.id.trackAlbum)
        val hasAlbum = track.collectionName != null
        trackAlbum.text = track.collectionName
        trackAlbum.visibility = if (hasAlbum) View.VISIBLE else View.GONE
        labelTrackAlbum.visibility = if (hasAlbum) View.VISIBLE else View.GONE

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}