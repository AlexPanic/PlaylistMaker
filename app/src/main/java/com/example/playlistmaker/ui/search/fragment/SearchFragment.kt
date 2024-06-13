package com.example.playlistmaker.ui.search.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.SearchState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.enums.ApiResultIcons
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.search.TrackListAdapter
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val SEARCH_STRING = "SEARCH_STRING"
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchViewModel>()
    private var searchMask: String = ""
    private lateinit var tracksRv: RecyclerView
    private lateinit var historyRv: RecyclerView
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val adapterHistory =
        TrackListAdapter(clickListener = object : TrackListAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                onTrackClickDebounce(track)
            }

        })

    private val adapter =
        TrackListAdapter(clickListener = object : TrackListAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                viewModel.addToHistory(track)
                onTrackClickDebounce(track)
            }
        })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchMask = savedInstanceState?.getString(SEARCH_STRING, "").orEmpty()
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
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

        // подпишемся на изменения
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        // адаптер поиска
        tracksRv = binding.rvTracks
        tracksRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        tracksRv.adapter = adapter

        // адаптер истории
        historyRv = binding.rvTracksHistory
        historyRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        historyRv.adapter = adapterHistory

        // установим сохраненное значение маски
        binding.tilSearchTracksField.editText?.setText(searchMask)

        // следим за изменением в поисковой строке
        binding.tilSearchTracksField.editText?.doOnTextChanged { text, _, _, _ ->
            searchMask = text.toString()
            if (searchMask.isEmpty()) {
                viewModel.stopSearch()
                if (binding.tilSearchTracksField.hasFocus()) {
                    viewModel.showHistory()
                }
            } else {
                viewModel.searchDebounce(searchMask, false)
            }
        }

        // очистка поискового запроса кнопкой
        binding.tilSearchTracksField.setEndIconOnClickListener {
            binding.tilSearchTracksField.editText?.setText("")
            viewModel.showHistory()
            hideKeyboard()
        }

        // покажем историю поиска при выполнении условий
        binding.tilSearchTracksField.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (
                hasFocus && binding.tilSearchTracksField.editText?.text.toString()
                    .isEmpty()
            ) {
                viewModel.showHistory()
            }
        }

        // очистка истории поиска
        binding.btTrackHistoryClear.setOnClickListener {
            viewModel.clearHistory()
        }

        // кнопка перезапуска последнего поискового запроса
        binding.btSearchReload.setOnClickListener {
            if (searchMask.isNotEmpty()) {
                binding.tilSearchTracksField.editText?.setText(searchMask)
                viewModel.searchDebounce(searchMask, force = true)
            }
        }

    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.HistoryClear -> hideAll()
            is SearchState.HistoryList -> showSearchHistory(tracks = state.tracks)
        }
    }

    private fun showLoading() {
        hideAll()
        binding.searchProgressBar.isVisible = true
        hideKeyboard()
    }

    private fun showContent(tracks: List<Track>) {
        hideAll()
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
        binding.rvTracks.scrollToPosition(0)
        binding.rvTracks.isVisible = true
    }

    private fun showEmpty(emptyMessage: String) {
        hideAll()
        with(binding) {
            ivApiResponseIcon.setImageResource(ApiResultIcons.NOTHING_FOUND.drawableId)
            ivApiResponseIcon.isVisible = true
            tvApiResponseMessage.text = emptyMessage
            tvApiResponseMessage.isVisible = true
        }
    }

    private fun showError(errorMessage: String) {
        hideAll()
        with(binding) {
            btSearchReload.isVisible = true
            ivApiResponseIcon.setImageResource(ApiResultIcons.SOMETHING_WENT_WRONG.drawableId)
            binding.ivApiResponseIcon.isVisible = true
            tvApiResponseMessage.text = errorMessage
            tvApiResponseMessage.isVisible = true
        }
    }

    private fun showSearchHistory(tracks: List<Track>) {
        adapterHistory.tracks = tracks as ArrayList<Track>
        adapterHistory.notifyDataSetChanged()
        hideAll()
        binding.llSearchHistory.isVisible = tracks.isNotEmpty()
    }

    private fun hideAll() {
        with(binding) {
            btSearchReload.isVisible = false
            ivApiResponseIcon.isVisible = false
            llSearchHistory.isVisible = false
            rvTracks.isVisible = false
            searchProgressBar.isVisible = false
            tvApiResponseMessage.isVisible = false
        }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        val view: View? = activity?.currentFocus
        if (view != null) {
            val inputMethodManager =
                activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING, searchMask)
    }

    override fun onStop() {
        viewModel.saveHistory()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}