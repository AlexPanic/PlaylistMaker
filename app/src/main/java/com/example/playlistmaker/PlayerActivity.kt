package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlayerActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val track = intent.getSerializableExtra(Track.INTENT_EXTRA_ID, Track::class.java) as Track

        // увеличенная обложка
        val trackCover = findViewById<ImageView>(R.id.trackCover)
        val coverUrl = track.getArtworkUrl512()
        val cornersRadius = 8f
        Glide.with(applicationContext).load(coverUrl).fitCenter()
            .transform(RoundedCorners(Helper.dpToPx(cornersRadius))).into(trackCover)

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
        val hasAlbum = track.collectionName.isNotEmpty()
        trackAlbum.text = track.collectionName
        trackAlbum.isVisible = hasAlbum
        labelTrackAlbum.isVisible = hasAlbum

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}