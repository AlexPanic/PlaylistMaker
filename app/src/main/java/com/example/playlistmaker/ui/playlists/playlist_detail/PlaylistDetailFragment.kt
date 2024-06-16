package com.example.playlistmaker.ui.playlists.playlist_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailBinding
import com.example.playlistmaker.domain.playlists.PlaylistDetailState
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.ui.common.Helper
import com.example.playlistmaker.ui.playlists.playlist_detail.view_model.PlaylistDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailFragment : Fragment() {
    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = binding.toolbar
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val playlistId = requireArguments().getLong(PLAYLIST_ID)
        viewModel.loadPlaylist(playlistId)
        viewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistDetailState.Loading -> {}
                is PlaylistDetailState.Content -> {
                    showPlaylistDetails(it.playlist, it.trackTimeTotalMinutes)
                }
            }
        }
    }

    private fun showPlaylistDetails(playlist: Playlist, trackTimeTotalMinutes: String) {
        binding.tvPlaylistName.text = playlist.name
        binding.tvPlaylistDescription.text = playlist.description
        binding.tvPlaylistDescription.isVisible = !playlist.description.isNullOrBlank()
        binding.tvPlaylistSummary.text =
            trackTimeTotalMinutes + " минут • ${playlist.tracksCount} треков"
        if (playlist.cover != null) {
            Glide.with(this)
                .load(playlist.cover)
                .transform(CenterCrop(), RoundedCorners(Helper.dpToPx(Helper.COVER_RADIUS)))
                .into(binding.ivPlaylistCover)
        } else {
            binding.ivPlaylistCover.setImageResource(R.drawable.cover_placeholder)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PLAYLIST_ID = "playlistId"
        fun createArgs(playlistId: Long): Bundle = bundleOf("playlistId" to playlistId)
    }
}