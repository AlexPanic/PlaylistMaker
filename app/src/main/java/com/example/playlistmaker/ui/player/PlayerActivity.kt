package com.example.playlistmaker.ui.player

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.common.Helper
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var trackIsPlaying = false
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private lateinit var playerInteract: PlayerInteract

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        Helper.setToolbar(this)

        // получаем модель трека
        val track = intent.getSerializableExtra(Track.INTENT_EXTRA_ID, Track::class.java) as Track

        // элементы view для интеракций
        val tvTrackPlayPosition = findViewById<TextView>(R.id.tvTrackPlayPosition)
        val btPlayControl = findViewById<Button>(R.id.btPlayControl)

        // апдейтер
        val playerUpdater = object : PlayerUiUpdater {
            override fun onPlayerDefault() {
                trackIsPlaying = false
            }

            override fun onPlayerPrepared() {
                trackIsPlaying = false
                btPlayControl.isEnabled = true
            }

            override fun onPlayerPlaying() {
                trackIsPlaying = true
                btPlayControl.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.pause_btn
                )
            }

            override fun onPlayerPaused() {
                trackIsPlaying = false
                btPlayControl.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.play_btn
                )
            }

            override fun onPlayerPlaybackCompleted() {
                trackIsPlaying = false
                tvTrackPlayPosition.text = dateFormat.format(0)
                btPlayControl.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.play_btn
                )
            }

            override fun onPositionChange(playPositionMillis: Int) {
                tvTrackPlayPosition.text = dateFormat.format(playPositionMillis)
            }

            override fun isTrackPlaying(): Boolean {
                return trackIsPlaying
            }
        }

        // интерактор плеера
        playerInteract = PlayerInteract(playerUpdater)
        playerInteract.init(track.previewUrl)

        btPlayControl.setOnClickListener {
            playerInteract.playPauseToggle(trackIsPlaying)
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

    override fun onPause() {
        super.onPause()
        playerInteract.onActivityPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteract.onActivityDestroy()
    }

}