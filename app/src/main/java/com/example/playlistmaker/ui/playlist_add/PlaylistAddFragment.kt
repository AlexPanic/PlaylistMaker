package com.example.playlistmaker.ui.playlist_add

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistAddBinding
import com.example.playlistmaker.domain.playlists.PlaylistAddState
import com.example.playlistmaker.ui.common.Helper
import com.example.playlistmaker.ui.playlist_add.view_model.PlaylistAddViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistAddFragment : Fragment() {

    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private var _binding: FragmentPlaylistAddBinding? = null
    private val binding get() = _binding!!
    private val requester = PermissionRequester.instance()
    private val playlistAddViewModel by viewModel<PlaylistAddViewModel>()
    private var coverUri: Uri? = null
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backPressedDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        _binding = FragmentPlaylistAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun backPressedPassed() {
        callback.isEnabled = false
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun backPressedDialog() {
        if ((coverUri != null)
            || binding.newPlaylistName.text.toString().isNotEmpty()
            || binding.newPlaylistDescription.text.toString().isNotEmpty()
        ) {
            confirmDialog = MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.playlist_add_backpressed_title))
                .setMessage(getString(R.string.playlist_add_backpressed_message))
                .setNeutralButton(getString(R.string.label_cancel)) { _, _ ->

                }.setPositiveButton(getString(R.string.label_finish)) { _, _ ->
                    backPressedPassed()
                }
            confirmDialog.show()
        } else {
            backPressedPassed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = binding.toolbar
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        playlistAddViewModel.observeState().observe(viewLifecycleOwner) {
            when(it) {
                is PlaylistAddState.Added -> backPressedPassed()
                else -> {}
            }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    coverUri = uri
                    Glide
                        .with(this)
                        .load(uri)
                        .transform(CenterCrop(), RoundedCorners(Helper.dpToPx(Helper.COVER_RADIUS)))
                        .into(binding.ivPlaylistCover)
                }
            }

        binding.tilNewPlaylistName.editText?.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylistBtn.isEnabled = text.toString().isNotEmpty()
        }

        binding.createPlaylistBtn.setOnClickListener {
            lifecycleScope.launch {
                playlistAddViewModel.addPlaylist(
                    binding.newPlaylistName.text.toString(),
                    binding.newPlaylistDescription.text.toString(),
                    coverUri
                )
            }
        }

        val permissionsAr =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        binding.ivPlaylistCover.setOnClickListener {
            lifecycleScope.launch {
                requester.request(*permissionsAr).collect { result ->
                    when (result) {
                        is PermissionResult.Granted -> {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }

                        is PermissionResult.Denied.DeniedPermanently -> {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.data = Uri.fromParts("package", context?.packageName, null)
                            context?.startActivity(intent)
                        }

                        is PermissionResult.Denied.NeedsRationale -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.permission_images_rationale,
                                Toast.LENGTH_LONG
                            ).show()
                            binding.createPlaylistBtn.isEnabled = false
                        }

                        is PermissionResult.Cancelled -> {
                            return@collect
                        }
                    }


                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}