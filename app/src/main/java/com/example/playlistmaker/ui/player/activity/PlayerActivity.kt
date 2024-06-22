package com.example.playlistmaker.ui.player.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.playlists.AddTrackToPlaylistState
import com.example.playlistmaker.domain.playlists.PlaylistsState
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.common.Helper
import com.example.playlistmaker.ui.enums.PlayerState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.playlists.playlist_add.PlaylistAddFragment
import com.example.playlistmaker.ui.playlists.playlists_list.PlaylistsAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()
    private var trackIsFavorite: Boolean = false
    private var adapter: PlaylistsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setSupportActionBar(binding.toolbar)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener{
            this.onBackPressedDispatcher.onBackPressed()
        }

        val track =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(Track.INTENT_EXTRA_ID, Track::class.java) as Track
            } else {
                intent.getSerializableExtra(Track.INTENT_EXTRA_ID) as Track
            }
        setViewsData(track)

        viewModel.prepare(track.previewUrl, track.trackId)
        viewModel.observeState().observe(this) {
            renderState(it)
        }
        viewModel.observePosition().observe(this) {
            renderPosition(it)
        }

        binding.btPlayControl.setOnClickListener {
            viewModel.playPause()
        }

        viewModel.observeIsFavorite().observe(this@PlayerActivity) {
            trackIsFavorite = it
            renderFavorite()
        }

        binding.btFavorite.setOnClickListener {
            lifecycleScope.launch {
                binding.btFavorite.isEnabled = false
                viewModel.toggleFavorite(trackIsFavorite, track)
            }
        }

        val bottomSheetContainer = findViewById<LinearLayout>(R.id.player_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        val overlay = findViewById<View>(R.id.overlay)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })


        adapter =
            PlaylistsAdapter(
                // адаптер плейлистов с шаблоном для экрана плеера
                layout = R.layout.list_item_playlist_on_player_screen,
                clickListener = object : PlaylistsAdapter.PlaylistClickListener {
                    override fun onPlaylistClick(playlist: Playlist) {
                        viewModel.addTrackToPlaylist(track, playlist)
                    }
                })
        val rvPlaylists = findViewById<RecyclerView>(R.id.rvPlaylists)
        rvPlaylists.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvPlaylists.adapter = adapter


        val btPlaylistAdd = findViewById<Button>(R.id.btPlaylistAdd)
        btPlaylistAdd.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            supportFragmentManager.beginTransaction()
                .add(R.id.playerFragmentContainerView, PlaylistAddFragment())
                .addToBackStack(getString(R.string.new_playlist))
                .commit()
            binding.toolbar.setTitle(getString(R.string.new_playlist))
        }

        // кнопка добавить трек в плейлист
        binding.btAddToPlaylist.setOnClickListener {
            // приоткроем bottomSheet
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            // покажем имеющиеся плейлисты
            viewModel.loadPlaylists()
        }

        viewModel.observePlaylists().observe(this) {
            when (it) {
                is PlaylistsState.Empty -> {}
                is PlaylistsState.Content -> {
                    adapter?.playlists?.clear()
                    adapter?.playlists?.addAll(it.playlists)
                    adapter?.notifyDataSetChanged()
                }

                is PlaylistsState.Loading -> {}
            }
        }

        // результат добавления трека в плейлист
        viewModel.observeAddResult().observe(this) {
            when (it) {
                // уже в плейлисте
                is AddTrackToPlaylistState.AlreadyContains ->
                    showToast(getString(R.string.already_in_playlist) + " " + it.playlistName)
                // добавлен
                is AddTrackToPlaylistState.Added -> {
                    showToast(getString(R.string.added_to_playlist) + " " + it.playlistName)
                }
            }
        }

    }

    private fun setViewsData(track: Track) {
        val coverUrl = track.getArtworkUrl512()
        with(binding) {
            with(track) {
                Glide.with(applicationContext)
                    .load(coverUrl)
                    .transform(FitCenter(), RoundedCorners(Helper.dpToPx(Helper.COVER_RADIUS)))
                    .into(ivTrackCover)
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

    private fun renderFavorite() {
        binding.btFavorite.background = when (trackIsFavorite) {
            true -> ContextCompat.getDrawable(this, R.drawable.btn_favorite_checked)
            false -> ContextCompat.getDrawable(this, R.drawable.btn_favorite)
        }
        binding.btFavorite.isEnabled = true
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            PlayerState.DEFAULT -> {}
            PlayerState.PREPARED -> {}
            PlayerState.ERROR -> {}

            PlayerState.PLAYING -> {
                binding.btPlayControl.background =
                    ContextCompat.getDrawable(this, R.drawable.pause_btn)
            }

            PlayerState.PAUSED,
            PlayerState.PLAYBACK_COMPLETE -> {
                binding.btPlayControl.background =
                    ContextCompat.getDrawable(this, R.drawable.play_btn)

            }
        }
    }

    private fun renderPosition(position: String) {
        binding.tvTrackPlayPosition.text = position
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
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

}