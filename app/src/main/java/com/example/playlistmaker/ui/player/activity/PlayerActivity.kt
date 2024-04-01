package com.example.playlistmaker.ui.player.activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.common.Helper
import com.example.playlistmaker.ui.enums.PlayerState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper.setToolbar(this)

        viewModel.observeState().observe(this) {
            renderState(it)
        }
        viewModel.observePosition().observe(this) {
            renderPosition(it)
        }

        val track =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(Track.INTENT_EXTRA_ID, Track::class.java) as Track
            } else {
                intent.getSerializableExtra(Track.INTENT_EXTRA_ID) as Track
            }
        setViewsData(track)
        viewModel.prepare(track.previewUrl)

        binding.btPlayControl.setOnClickListener {
            viewModel.playPause()
        }
    }

    private fun setViewsData(track: Track) {
        val coverUrl = track.getArtworkUrl512()
        with(binding) {
            with(track) {
                Glide.with(applicationContext).load(coverUrl).fitCenter()
                    .transform(RoundedCorners(Helper.dpToPx(COVER_CORNERS))).into(ivTrackCover)
                tvTrackName.text = trackName
                tvArtistName.text = artistName
                tvTrackDuration.text = trackTime()
                tvTrackYear.text = getYear()
                tvTrackGenre.text = primaryGenreName
                tvTrackCountry.text = country
                tvTrackAlbum.text = collectionName
                val hasAlbum = collectionName.isNotEmpty()
                tvTrackAlbum.isVisible = hasAlbum
                tvTrackAlbumTitle.isVisible = hasAlbum
            }
        }
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            PlayerState.DEFAULT -> {}
            PlayerState.PREPARED -> {}
            PlayerState.ERROR -> {}

            PlayerState.PLAYING -> {
                binding.btPlayControl.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.pause_btn)
            }

            PlayerState.PAUSED,
            PlayerState.PLAYBACK_COMPLETE -> {
                binding.btPlayControl.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.play_btn)

            }
        }
    }

    private fun renderPosition(position: String) {
        binding.tvTrackPlayPosition.text = position
    }

    override fun onPause() {
        super.onPause()
        viewModel.onActivityPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActivityResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val COVER_CORNERS = 8f
    }

}