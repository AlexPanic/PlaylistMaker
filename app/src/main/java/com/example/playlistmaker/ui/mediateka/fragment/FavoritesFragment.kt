package com.example.playlistmaker.ui.mediateka.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.mediateka.FavoritesState
import com.example.playlistmaker.ui.mediateka.view_model.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private val favoritesViewModel by viewModel<FavoritesViewModel> {
        parametersOf(requireArguments().getString(PlaylistsFragment.SOME_PARAM))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritesViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is FavoritesState.Error -> showError(it.message)
            }
        }
    }

    private fun showError(message: String) {
        binding.apply {
            errorMessage.text = message
        }
    }

    companion object {
        const val SOME_PARAM = "paramName"
        fun newInstance(paramName: String) = FavoritesFragment().apply {
            arguments = bundleOf(SOME_PARAM to paramName)
        }
    }

}