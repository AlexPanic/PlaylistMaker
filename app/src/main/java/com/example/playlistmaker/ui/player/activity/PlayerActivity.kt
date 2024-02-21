package com.example.playlistmaker.ui.player.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.player.PlayerInteract_
import com.example.playlistmaker.domain.player.PlayerUiUpdater
import com.example.playlistmaker.domain.player.model.PlayerState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.common.Helper
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var trackIsPlaying = false
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private lateinit var playerInteract: PlayerInteract_

    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityPlayerBinding


    private fun setViewsData(track: Track) {
        val coverUrl = track.getArtworkUrl512()
        with(binding) {
            with(track) {
                Glide.with(applicationContext).load(coverUrl).fitCenter()
                    .transform(RoundedCorners(Helper.dpToPx(COVER_CORNERS))).into(ivTrackCover)
                tvTrackName.text = trackName
                tvArtistName.text = artistName
                tvTrackDuration.text = trackTime()
                tvTrackYear.text =getYear()
                tvTrackGenre.text = primaryGenreName
                tvTrackCountry.text = country
                tvTrackAlbum.text = collectionName
                val hasAlbum = collectionName.isNotEmpty()
                tvTrackAlbum.isVisible = hasAlbum
                tvTrackAlbumTitle.isVisible = hasAlbum
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun render(state: PlayerState) {
        when (state) {
            PlayerState.Default -> {
                val track = intent.getSerializableExtra(Track.INTENT_EXTRA_ID, Track::class.java) as Track
                setViewsData(track)
                viewModel.prepare(track.previewUrl)
            }
            PlayerState.Prepared -> {
                binding.btPlayControl.setBackgroundTintList(this.resources.getColorStateList(R.color.filled_button_tint))
            }

            else -> {}
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper.setToolbar(this)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory()
        )[PlayerViewModel::class.java]

        // подпишемся на модель
        viewModel.observeState().observe(this) {
            Log.d("mine", "PlayerState = " + it.toString())
            render(it)
        }

        binding.btPlayControl.setOnClickListener{
            viewModel.play()
        }

/*
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
        playerInteract = PlayerInteract_(playerUpdater)
        //playerInteract.init(track.previewUrl)

        btPlayControl.setOnClickListener {
            playerInteract.playPauseToggle(trackIsPlaying)
        }
*/

    }

    override fun onPause() {
        super.onPause()
        //playerInteract.onActivityPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //playerInteract.onActivityDestroy()
    }

    companion object {
        private const val COVER_CORNERS = 8f
    }

}