package com.example.playlistmaker.ui.mediateka.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.mediateka.PlaylistsState
import com.example.playlistmaker.ui.mediateka.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistsBinding
    private val playlistsViewModel by viewModel<PlaylistsViewModel> {
        parametersOf(requireArguments().getString(SOME_PARAM))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistsViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistsState.Error -> showError(it.message)
                is PlaylistsState.Content -> {
                }
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
        fun newInstance(paramName: String) = PlaylistsFragment().apply {
            arguments = bundleOf(SOME_PARAM to paramName)
        }
    }

}