package com.example.playlistmaker.presentation.ui.player

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.ui.common.Helper
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.PlayerInteract
import com.example.playlistmaker.presentation.PlayerUiUpdater
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var playButton: Button? = null
    private var trackPositionTimer: TextView? = null
    private var trackIsPlaying = false
    private var playerInteract: PlayerInteract? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        Helper.setToolbar(this)

        // получаем модель трека
        val track = Creator.provideGetTrackUseCase(intent).execute()

        // элементы view для интеракций
        trackPositionTimer = findViewById(R.id.tvTrackPlayPosition)
        playButton = findViewById(R.id.btPlayControl)

        // апдейтер
        val playerUpdater = object: PlayerUiUpdater {
            override fun onPlayerDefault() {
                trackIsPlaying = false
            }
            override fun onPlayerPrepared() {
                trackIsPlaying = false
                playButton!!.isEnabled = true
            }
            override fun onPlayerPlaying() {
                trackIsPlaying = true
                playButton!!.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.pause_btn
                )
            }
            override fun onPlayerPaused() {
                trackIsPlaying = false
                playButton!!.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.play_btn
                )
            }
            override fun onPlayerPlaybackCompleted() {
                trackIsPlaying = false
                trackPositionTimer!!.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)
                playButton!!.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.play_btn
                )
            }
            override fun onPositionChange(playPositionMillis: Int) {
                trackPositionTimer!!.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(playPositionMillis)
            }
            override fun isTrackPlaying(): Boolean {
                return trackIsPlaying
            }
        }

        // интерактор плеера
        playerInteract = PlayerInteract(playerUpdater)
        playerInteract!!.init(track.previewUrl)

        playButton!!.setOnClickListener {
            playerInteract!!.playPauseToggle(trackIsPlaying)
        }

        // задаем значения полей из модели
        val trackCover = findViewById<ImageView>(R.id.ivTrackCover)
        val coverUrl = track.getArtworkUrl512()
        val cornersRadius = 8f
        Glide.with(applicationContext).load(coverUrl).fitCenter()
            .transform(RoundedCorners(Helper.dpToPx(cornersRadius))).into(trackCover)
        // свойства трека
        val trackName = findViewById<TextView>(R.id.tvTrackName)
        trackName.text = track.trackName
        val artistName = findViewById<TextView>(R.id.tvArtistName)
        artistName.text = track.artistName
        val trackTime = findViewById<TextView>(R.id.tvTrackDuration)
        trackTime.text = track.trackTime()
        val trackYear = findViewById<TextView>(R.id.tvTrackYear)
        trackYear.text = track.getYear()
        val trackGenre = findViewById<TextView>(R.id.tvTrackGenre)
        trackGenre.text = track.primaryGenreName
        val trackCountry = findViewById<TextView>(R.id.tvTrackCountry)
        trackCountry.text = track.country
        // альбом, если у трека он есть
        val labelTrackAlbum = findViewById<TextView>(R.id.tvTrackAlbumTitle)
        val trackAlbum = findViewById<TextView>(R.id.tvTrackAlbum)
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

    override fun onPause() {
        super.onPause()
        playerInteract!!.onActivityPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteract!!.onActivityDestroy()
    }

}