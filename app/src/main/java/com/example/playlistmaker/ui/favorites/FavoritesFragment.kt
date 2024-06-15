package com.example.playlistmaker.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.favorites.FavoritesState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.favorites.view_model.FavoritesViewModel
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val favoritesViewModel by viewModel<FavoritesViewModel>()
    private var adapter: FavoritesAdapter? = null
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
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

        adapter = FavoritesAdapter(clickListener = object : FavoritesAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                onTrackClickDebounce(track)
            }
        })
        binding.favoritesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesList.adapter = adapter

        favoritesViewModel.fillData()
        favoritesViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is FavoritesState.Loading -> showLoading()
                is FavoritesState.Empty -> showEmpty(it.message)
                is FavoritesState.Content -> showContent(it.tracks)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading() {
        with(binding) {
            errorIcon.isVisible = false
            errorMessage.isVisible = false
            progressBar.isVisible = true
            favoritesList.isVisible = false
        }
    }

    private fun showEmpty(message: String) {
        with(binding) {
            progressBar.isVisible = false
            errorIcon.isVisible = true
            errorMessage.text = message
            errorMessage.isVisible = true
            favoritesList.isVisible = false
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            errorIcon.isVisible = false
            errorMessage.isVisible = false
            progressBar.isVisible = false
            favoritesList.isVisible = true
        }
        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        favoritesViewModel.fillData()
    }

    companion object {
        fun newInstance() = FavoritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

}