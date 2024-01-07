package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var playerState = STATE_DEFAULT
    private var isClickAllowed = true

    private lateinit var playButton: Button
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler: Handler? = null
    private var runnable: Runnable? = null
    lateinit var trackPositionTimer: TextView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        trackPositionTimer = findViewById(R.id.trackPos)
        playButton = findViewById(R.id.track_play_button)

        val track = intent.getSerializableExtra(Track.INTENT_EXTRA_ID, Track::class.java) as Track

        mainThreadHandler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                refreshTimer()
            }
        }

        playButton.setOnClickListener {
            playbackControl()
        }
        preparePlayer(track.previewUrl)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        releaseTimer()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler!!.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                if (clickDebounce()) {
                    pausePlayer()
                }
            }

            STATE_PREPARED, STATE_PAUSED -> {
                if (clickDebounce()) {
                    startPlayer()
                }
            }
        }

    }

    private fun togglePlayButton(setAsPlay: Boolean) {
        playButton.background = ContextCompat.getDrawable(
            applicationContext,
            if (setAsPlay) R.drawable.play_btn else R.drawable.pause_btn
        )
    }

    private fun preparePlayer(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            togglePlayButton(true)
            playerState = STATE_PREPARED
            releaseTimer()
            trackPositionTimer.text = getString(R.string.player_track_stop_position)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        togglePlayButton(false)
        playerState = STATE_PLAYING
        mainThreadHandler?.post(runnable as Runnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        togglePlayButton(true)
        playerState = STATE_PAUSED
        releaseTimer()
    }

    private fun releaseTimer() {
        mainThreadHandler!!.removeCallbacks(runnable!!)
    }

    private fun refreshTimer() {
        trackPositionTimer.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        if (playerState == STATE_PLAYING) {
            mainThreadHandler?.postDelayed(
                runnable!!, 250
            )
        } else {
            releaseTimer()
        }
    }
}