package com.example.playlistmaker.ui.mediateka.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.mediateka.FavoritesState
import com.example.playlistmaker.ui.mediateka.view_model.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val favoritesViewModel by viewModel<FavoritesViewModel>()

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
        favoritesViewModel.fillData()
        favoritesViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is FavoritesState.Loading -> showLoading()
                is FavoritesState.Empty -> showEmpty(it.message)
                is FavoritesState.Content -> showEmpty("CONTENT")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading() {
        with (binding) {
            errorIcon.isVisible = false
            errorMessage.isVisible = false
            progressBar.isVisible = true
        }
    }

    private fun showEmpty(message: String) {
        with (binding) {
            progressBar.isVisible = false
            errorIcon.isVisible = true
            errorMessage.text = message
            errorMessage.isVisible = true
        }
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }

}