package com.example.playlistmaker.ui.playlists.playlist_detail

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import com.example.playlistmaker.ui.playlists.playlist_add.PlaylistAddFragment
import com.example.playlistmaker.ui.playlists.playlist_detail.view_model.PlaylistDetailViewModel
import com.example.playlistmaker.ui.search.TrackListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistDetailFragment : Fragment() {
    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistDetailViewModel>()
    private var playlistId: Long = 0
    private lateinit var playlist: Playlist
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var bsTracksContainer: LinearLayout
    private lateinit var bsPlaylistOptionsContainer: LinearLayout
    private lateinit var overlay: View
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
                    is PlaylistDetailState.Deleted -> {
                        showToast(getString(R.string.playlist_deleted))
                        findNavController().navigate(
                            R.id.action_playlistFragment_to_mediatekaFragment
                        )
                    }
                    else -> {}
                }
            }

            val tracksRv = requireActivity().findViewById<RecyclerView>(R.id.rvTracks)
            tracksRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            tracksRv.adapter = adapter
            viewModel.observeTracks().observe(viewLifecycleOwner) {
                when (it) {
                    is PlaylistTracksState.Content -> {
                        showTracks(it.tracks)
                    }

                    is PlaylistTracksState.Empty -> {
                        showEmpty(getString(R.string.no_tracks_in_playlist))
                    }

                    else -> {}
                }
            }

        }

        // затенение
        overlay = requireActivity().findViewById<View>(R.id.overlay)

        val screenHeight = requireActivity().resources.displayMetrics.heightPixels
        // контейнер треков
        bsTracksContainer = requireActivity().findViewById<LinearLayout>(R.id.bottom_sheet)
        bsTracksContainer.isVisible = true
        val bsTracksBehavior = BottomSheetBehavior.from(bsTracksContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            if (screenHeight<1920) {
                peekHeight = 320
            }
        }
        // затенение на растягивание списка треков в плейлисте
        bsTracksBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.isVisible = newState != BottomSheetBehavior.STATE_COLLAPSED
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        // контейнер управления плейлистом
        bsPlaylistOptionsContainer =
            requireActivity().findViewById<LinearLayout>(R.id.bottom_sheet2)
        bsPlaylistOptionsContainer.isVisible = true
        val bsPlaylistOptionsBehavior = BottomSheetBehavior.from(bsPlaylistOptionsContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            //peekHeight = 100
        }
        // затенение на растягивание списка треков в плейлисте
        bsPlaylistOptionsBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        // кнопка поделиться
        binding.btPlaylistShare.setOnClickListener {
            sharePlaylist()
        }

        // меню управления плейлистом
        binding.btPlaylistMenu.setOnClickListener {
            bsPlaylistOptionsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            overlay.isVisible = true
        }

        // ссылка поделиться в опциях
        val optionPlaylistShare = requireActivity().findViewById<TextView>(R.id.playlist_option_share)
        optionPlaylistShare.setOnClickListener{
            sharePlaylist()
        }

        // ссылка на редактирование в опциях
        val optionPlaylistEdit = requireActivity().findViewById<TextView>(R.id.playlist_option_edit)
        optionPlaylistEdit.setOnClickListener{
            findNavController()
                .navigate(
                    R.id.action_playlistFragment_to_playlistAddFragment,
                    PlaylistAddFragment.createArgs(Gson().toJson(playlist))
                )
        }

        // ссылка удаление в опциях
        val optionPlaylistDelete = requireActivity().findViewById<TextView>(R.id.playlist_option_delete)
        optionPlaylistDelete.setOnClickListener{

            viewModel.deletePlaylist(playlist)
            /*confirmDialog = MaterialAlertDialogBuilder(requireActivity())
                .setMessage(getString(R.string.playlist_delete_title, playlist.name))
                .setNeutralButton(getString(R.string.label_no)) { _, _ ->

                }.setPositiveButton(getString(R.string.label_yes)) { _, _ ->
                    viewModel.deletePlaylist(playlist)
                }
            confirmDialog.show()*/
        }



    }

    private fun sharePlaylist() {
        if (adapter.tracks.isEmpty()) {
            showToast(getString(R.string.no_tracks_for_share_playlist))
        } else {
            viewModel.sharePlaylist(playlist, adapter.tracks)
        }
    }

    private fun showPlaylistDetails(playlist: Playlist, trackTimeTotalMinutes: Int) {

        val optPlaylistCover = requireActivity().findViewById<ImageView>(R.id.ivPlaylistCover)
        val optPlaylistName = requireActivity().findViewById<TextView>(R.id.tvPlaylistName)
        val optPlaylistSummary = requireActivity().findViewById<TextView>(R.id.tvTracksCount)

        optPlaylistName.text = playlist.name
        optPlaylistSummary.text = getString(
            R.string.playlist_tracks_count,
            playlist.tracksCount,
            resources.getQuantityString(R.plurals.tracks, playlist.tracksCount)
        )

        binding.tvPlaylistDetailName.text = playlist.name
        binding.tvPlaylistDetailDescription.text = playlist.description
        binding.tvPlaylistDetailDescription.isVisible = !playlist.description.isNullOrBlank()
        binding.tvPlaylistDetailSummary.text = getString(
            R.string.playlist_summary,
            trackTimeTotalMinutes,
            resources.getQuantityString(R.plurals.minutes, trackTimeTotalMinutes),
            playlist.tracksCount,
            resources.getQuantityString(R.plurals.tracks, playlist.tracksCount)
        )
        if (playlist.cover != null) {
            val glideCover =
                Glide.with(this)
                    .load(playlist.cover)
                    .transform(CenterCrop(), RoundedCorners(Helper.dpToPx(Helper.COVER_RADIUS)))
            glideCover.into(optPlaylistCover)
            glideCover.into(binding.ivPlaylistDetailCover)
        } else {
            binding.ivPlaylistDetailCover.setImageResource(R.drawable.cover_placeholder)
            optPlaylistCover.setImageResource(R.drawable.cover_placeholder)
        }
    }

    private fun showEmpty(message: String) {
        val errorTv = requireActivity().findViewById<TextView>(R.id.bottomSheetErrorMessage)
        errorTv.text = message
        errorTv.isVisible = true
        adapter.tracks.clear()
        adapter.notifyDataSetChanged()
    }

    private fun showTracks(tracks: List<Track>) {
        val errorTv = requireActivity().findViewById<TextView>(R.id.bottomSheetErrorMessage)
        errorTv.text = ""
        errorTv.isVisible = false
        adapter.tracks = tracks as ArrayList<Track>
        adapter.notifyDataSetChanged()
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bsTracksContainer.isVisible = false
        bsPlaylistOptionsContainer.isVisible = false
        overlay.isVisible = false
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