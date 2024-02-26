package com.example.playlistmaker.ui.search.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.model.SearchState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.common.Helper
import com.example.playlistmaker.ui.enums.ApiResultIcons
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.search.TrackListAdapter
import com.example.playlistmaker.ui.search.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_STRING = "SEARCH_STRING"
    }

    private fun startPlayer(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
            intent.putExtra(
                Track.INTENT_EXTRA_ID, track
            )
            startActivity(intent)
        }
    }

    private val adapterHistory =
        TrackListAdapter(clickListener = object : TrackListAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                startPlayer(track)
            }

        })

    private val adapter =
        TrackListAdapter(clickListener = object : TrackListAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                viewModel.addToHistory(track)
                startPlayer(track)
            }
        })


    private var isClickAllowed = true
    private var searchMask: String = ""
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: ActivitySearchBinding
    private lateinit var tracksRv: RecyclerView
    private lateinit var historyRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Helper.setToolbar(this)

        viewModel = ViewModelProvider(
            this, SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        // подпишемся на изменения
        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.observeShowToast().observe(this) {
            showToast(it)
        }

        tracksRv = binding.rvTracks
        tracksRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksRv.adapter = adapter

        // адаптер истории поиска
        historyRv = binding.rvTracksHistory
        historyRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyRv.adapter = adapterHistory

        // установим сохраненное значение маски
        binding.tilSearchTracksField.editText?.setText(searchMask)

        // следим за изменением в поисковой строке
        binding.tilSearchTracksField.editText?.doOnTextChanged { text, _, _, _ ->
            searchMask = text.toString()
            if (binding.tilSearchTracksField.hasFocus() && searchMask.isEmpty()) {
                viewModel.showHistory()
            } else if (searchMask.isNotEmpty()) {
                viewModel.searchDebounce(searchMask)
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
                viewModel.searchDebounce(searchMask)
            }
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Error -> showEmpty(state.errorMessage)
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
        binding.rvTracks.isVisible = true
    }

    private fun showEmpty(emptyMessage: String) {
        showError(emptyMessage)
        binding.ivApiResponseIcon.setImageResource(ApiResultIcons.NOTHING_FOUND.drawableId)
        binding.ivApiResponseIcon.isVisible = true
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING, searchMask)
    }

    override fun onRestoreInstanceState(outState: Bundle) {
        super.onRestoreInstanceState(outState)
        searchMask = outState.getString(SEARCH_STRING, "")
    }

    override fun onStop() {
        viewModel.saveHistory()
        super.onStop()
    }

}