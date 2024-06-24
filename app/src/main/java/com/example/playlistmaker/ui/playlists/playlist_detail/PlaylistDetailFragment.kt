package com.example.playlistmaker.ui.playlists.playlist_detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.databinding.FragmentPlaylistDetailBinding
import com.example.playlistmaker.domain.playlists.PlaylistDetailState
import com.example.playlistmaker.domain.playlists.PlaylistTracksState
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.common.Helper
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.playlists.playlist_detail.view_model.PlaylistDetailViewModel
import com.example.playlistmaker.ui.search.TrackListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailFragment : Fragment() {
    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistDetailViewModel>()
    private var playlistId: Long = 0
    private var playlist: Playlist? = null
    private lateinit var bottomSheetContainer: LinearLayout
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val adapter =
        TrackListAdapter(clickListener = object : TrackListAdapter.TrackClickListener {
            override fun onClick(track: Track) {
                onTrackClickDebounce(track)
            }

            override fun onLongClick(track: Track): Boolean {
                confirmDialog = MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(getString(R.string.playlist_remove_track_title))
                    .setMessage(getString(R.string.playlist_remove_track_message))
                    .setNeutralButton(getString(R.string.label_no)) { _, _ ->

                    }.setPositiveButton(getString(R.string.label_yes)) { _, _ ->
                        viewModel.removeTrackFromPlaylist(track.trackId, playlistId)
                    }
                confirmDialog.show()
                return true
            }
        })

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

        onTrackClickDebounce = debounce<Track>(
            delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = true,
        ) { track ->
            val intent = Intent(activity, PlayerActivity::class.java)
            intent.putExtra(
                Track.INTENT_EXTRA_ID, track
            )
            startActivity(intent)
        }

        playlistId = requireArguments().getLong(PLAYLIST_ID)
        if (playlistId > 0) {
            viewModel.loadPlaylist(playlistId)
            viewModel.observeState().observe(viewLifecycleOwner) {
                when (it) {
                    is PlaylistDetailState.Content -> {
                        playlist = it.playlist
                        showPlaylistDetails(it.playlist, it.trackTimeTotalMinutes)
                    }

                    else -> {}
                }
            }

            val tracksRv = requireActivity().findViewById<RecyclerView>(R.id.rvTracks)
            tracksRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            tracksRv.adapter = adapter
            viewModel.observeTracks().observe(viewLifecycleOwner) {

                Log.d("mine", "observeTracks = $it")
                when (it) {
                    is PlaylistTracksState.Content -> {
                        showTracks(it.tracks)
                    }

                    else -> {}
                }
            }

        }

        binding.btPlaylistShare.setOnClickListener {
            if (adapter.tracks.isEmpty()) {
                showToast(getString(R.string.no_tracks_for_share_playlist))
            } else if (playlist != null) {
                viewModel.sharePlaylist(playlist!!, adapter.tracks)
            }
        }

        bottomSheetContainer = requireActivity().findViewById(R.id.bottom_sheet)
        bottomSheetContainer.isVisible = true

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            peekHeight = resources.displayMetrics.heightPixels - binding.btPlaylistShare.bottom
        }
        val overlay = requireActivity().findViewById<View>(R.id.overlay)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.isVisible = newState != BottomSheetBehavior.STATE_COLLAPSED
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun showPlaylistDetails(playlist: Playlist, trackTimeTotalMinutes: Int) {
        binding.tvPlaylistName.text = playlist.name
        binding.tvPlaylistDescription.text = playlist.description
        binding.tvPlaylistDescription.isVisible = !playlist.description.isNullOrBlank()
        binding.tvPlaylistSummary.text = getString(
            R.string.playlist_summary,
            trackTimeTotalMinutes,
            resources.getQuantityString(R.plurals.minutes, trackTimeTotalMinutes),
            playlist.tracksCount,
            resources.getQuantityString(R.plurals.tracks, playlist.tracksCount)
        )
        if (playlist.cover != null) {
            Glide.with(this)
                .load(playlist.cover)
                .transform(CenterCrop(), RoundedCorners(Helper.dpToPx(Helper.COVER_RADIUS)))
                .into(binding.ivPlaylistCover)
        } else {
            binding.ivPlaylistCover.setImageResource(R.drawable.cover_placeholder)
        }
    }

    private fun showTracks(tracks: List<Track>) {
        adapter.tracks = tracks as ArrayList<Track>
        adapter.notifyDataSetChanged()
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetContainer.isVisible = false
        adapter.tracks.clear()
        adapter.notifyDataSetChanged()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val PLAYLIST_ID = "playlistId"
        fun createArgs(playlistId: Long): Bundle = bundleOf("playlistId" to playlistId)
    }
}