package com.example.playlistmaker.ui.playlists.playlists_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.playlists.PlaylistsState
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.ui.playlists.playlist_add.PlaylistAddFragment
import com.example.playlistmaker.ui.playlists.playlist_detail.PlaylistDetailFragment
import com.example.playlistmaker.ui.playlists.playlists_list.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private var adapter: PlaylistsAdapter? = null
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPlaylistClickDebounce = debounce<Playlist>(
            delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false,
        ) { playlist ->
            findNavController()
                .navigate(
                    R.id.action_mediatekaFragment_to_playlistFragment,
                    PlaylistDetailFragment.createArgs(playlist.id)
                )
        }

        adapter = PlaylistsAdapter(
            layout = R.layout.list_item_playlist,
            clickListener = object : PlaylistsAdapter.PlaylistClickListener {
                override fun onPlaylistClick(playlist: Playlist) {
                    onPlaylistClickDebounce(playlist)
                }
            })

        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), COLUMNS)
        binding.rvPlaylists.adapter = adapter

        binding.btPlaylistAdd.setOnClickListener {
            findNavController()
                .navigate(
                    R.id.action_mediatekaFragment_to_playlistAddFragment,
                    PlaylistAddFragment.createArgs(null)
                )
        }
        playlistsViewModel.fillData()
        playlistsViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistsState.Empty -> showEmpty(it.message)
                is PlaylistsState.Content -> showContent(it.playlists)
                is PlaylistsState.Loading -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContent(playlists: List<Playlist>) {
        with(binding) {
            //progressBar.isVisible = false
            errorIcon.isVisible = false
            errorMessage.text = ""
            errorMessage.isVisible = true
            //playlistsList.isVisible = false
        }
        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(playlists)
        adapter?.notifyDataSetChanged()
    }

    private fun showEmpty(message: String) {
        with(binding) {
            //progressBar.isVisible = false
            errorIcon.isVisible = true
            errorMessage.text = message
            errorMessage.isVisible = true
            rvPlaylists
            //playlistsList.isVisible = false
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
        const val COLUMNS = 2
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

}