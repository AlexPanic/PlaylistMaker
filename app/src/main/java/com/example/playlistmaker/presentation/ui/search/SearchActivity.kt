package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.data.network.ItunesApiService
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.enums.ApiResultIcons
import com.example.playlistmaker.presentation.ui.common.Helper
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity(), TrackListAdapter.ItemClickListener {

    private var tracksList = mutableListOf<Track>()
    lateinit var adapter: TrackListAdapter
    private var tracksHistoryList = mutableListOf<Track>()
    lateinit var adapterHistory: TrackListAdapter
    lateinit var searchTextLayout: TextInputLayout
    private lateinit var searchInteract: SearchInteract


    // TODO: Ниже почистить ненужное
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    private var placeholderMessage: TextView? = null
    private var placeholderAlertIcon: ImageView? = null
    private var reSearchButton: Button? = null

    private var searchMask: String = ""
    private val searchRunnable = Runnable { searchRequest() }
    private var mainThreadHandler: Handler = Handler(Looper.getMainLooper())
    private val getSearchHistoryUseCase by lazy { Creator.provideGetSearchHistoryUseCase() }
    private val clearSearchHistoryUseCase by lazy { Creator.provideClearSearchHistoryUseCase() }
    private val saveSearchHistoryUseCase by lazy { Creator.provideSaveSearchHistoryUseCase() }
    private val findTracksUseCase by lazy { Creator.provideFindTracksUseCase(this) }

    private companion object {
        var SEARCH_STRING = "SEARCH_STRING"
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
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
        saveSearchHistoryUseCase.execute(adapterHistory.tracks)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        Helper.setToolbar(this)

        // адаптер истории поиска
        tracksHistoryList = getSearchHistoryUseCase.execute()
        val rvHistory = findViewById<RecyclerView>(R.id.rvTracksHistory)
        adapterHistory = TrackListAdapter(this, true).apply {
            tracks = this@SearchActivity.tracksHistoryList
        }
        rvHistory.adapter = adapterHistory

        // адаптер результатов поиска
        val rvTrackList = findViewById<RecyclerView>(R.id.rvTracks)
        adapter = TrackListAdapter(this, false).apply {
            tracks = tracksList
        }
        rvTrackList.adapter = adapter

        // область сообщений
        placeholderMessage = findViewById(R.id.tvApiResponseMessage)
        placeholderAlertIcon = findViewById(R.id.ivApiResponseIcon)
        // поле с маской поиска
        searchTextLayout = findViewById(R.id.tilSearchMask)
        // установим сохраненное значение
        searchTextLayout.editText?.setText(searchMask)

        // обновления в UI
        val searchUiUpdater = object : SearchUiUpdater {
            override fun onClearSearchMask() {
                searchTextLayout.editText?.setText("")
                showMessage("")
                showAlertIcon(ApiResultIcons.EMPTY)
                tracksList.clear()
                adapter.notifyDataSetChanged()
                updateSearchHistory()
                hideKeyboard()
            }

        }
        searchInteract = SearchInteract(searchUiUpdater)
        // очистка поискового запроса кнопкой
        searchTextLayout.setEndIconOnClickListener {
            searchInteract.clearSearchMask()
        }

        // покажем историю поиска при выполнении условий
        searchTextLayout.editText?.setOnFocusChangeListener { _, hasFocus ->
            searchHistoryVisibility(
                hasFocus
                        && searchTextLayout.editText?.text.toString().isEmpty()
                        && tracksHistoryList.isNotEmpty()
            )
        }

        // очистка истории поиска
        val btnSearchHistoryClear = findViewById<Button>(R.id.btTrackHistoryClear)
        btnSearchHistoryClear.setOnClickListener {
            clearSearchHistoryUseCase.execute()
        }

        // кнопка перезапуска последнего поискового запроса
        reSearchButton = findViewById(R.id.btSearchReload)
        reSearchButton!!.setOnClickListener {
            if (searchMask.isNotEmpty()) {
                searchTextLayout.editText?.setText(searchMask)
                searchRequest()
            }
        }

        // следим за изменением в поисковой строке
        searchTextLayout.editText!!.doOnTextChanged { text, _, _, _ ->
            searchMask = text.toString()
            searchHistoryVisibility(searchTextLayout.editText!!.hasFocus() && searchMask.isEmpty())
            searchDebounce()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // клик по треку в адаптере
    override fun onItemClick(track: Track, isHistoryAdapter: Boolean) {
        // в истории поиска
        if (!isHistoryAdapter) {
            with(adapterHistory) {
                // удалим трек, если он был (должен всплыть наверх)
                if (tracks.contains(track)) {
                    val pos = tracks.indexOf(track)
                    tracks.remove(track)
                    notifyItemRemoved(pos)
                    notifyItemRangeChanged(pos, tracks.size - 1)
                }
                // добавим трек в историю на 0 позицию
                tracks.add(0, track)
                notifyItemInserted(0)
                if (tracks.size > Constants.MAX_TRACKS) {
                    tracks.remove(tracks[Constants.MAX_TRACKS])
                    notifyItemRemoved(Constants.MAX_TRACKS)
                }
            }

        }
        // передаем трек на экран плеера
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(
            Track.INTENT_EXTRA_ID,
            track
        )
        startActivity(intent)
    }

    private fun searchHistoryVisibility(visible: Boolean) {
        val searchHistoryView = findViewById<ViewGroup>(R.id.llSearchHistory)
        searchHistoryView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updateSearchHistory() {
        tracksHistoryList = getSearchHistoryUseCase.execute()
        if (tracksHistoryList.isEmpty()) {
            searchHistoryVisibility(false)
        } else {
            adapterHistory.notifyDataSetChanged()
        }
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderMessage!!.isVisible = true
            tracksList.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage!!.text = text
        } else {
            placeholderMessage!!.isVisible = false
        }
    }


    private fun showAlertIcon(icon: ApiResultIcons) {
        placeholderAlertIcon!!.setImageResource(icon.drawableId)
        reSearchButton!!.isVisible = when (icon) {
            ApiResultIcons.SOMETHING_WENT_WRONG -> true
            else -> false
        }
    }

    private fun somethingWentWrong() {
        showMessage(getString(R.string.something_went_wrong))
        showAlertIcon(ApiResultIcons.SOMETHING_WENT_WRONG)
        hideKeyboard()
    }

    private fun showProgressBar(visible: Boolean) {
        val bar = findViewById<ProgressBar>(R.id.searchProgressBar)
        bar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    private fun searchRequest() {
        showAlertIcon(ApiResultIcons.EMPTY)
        showMessage("")
        if (searchMask.isEmpty()) return
        tracksList.clear()
        adapter.notifyDataSetChanged()
        showProgressBar(true)
        val found = findTracksUseCase.execute(searchMask)
        showProgressBar(false)
        if (found.isNotEmpty()) {
            tracksList = found.toMutableList()
            adapter.notifyDataSetChanged()
        } else {
            showMessage(getString(R.string.nothing_found))
            showAlertIcon(ApiResultIcons.NOTHING_FOUND)
        }

        /*
        itunesService.findTracks(searchMask)
            .enqueue(object : Callback<FindTracksResponse> {
                override fun onResponse(
                    call: Call<FindTracksResponse>,
                    response: Response<FindTracksResponse>
                ) {
                    showProgressBar(false)
                    when (response.code()) {
                        200 -> {
                            showAlertIcon(ApiResultIcons.EMPTY)
                            tracksList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracksList.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            } else {
                                showMessage(getString(R.string.nothing_found))
                                showAlertIcon(ApiResultIcons.NOTHING_FOUND)
                            }
                        }

                        else -> {
                            somethingWentWrong()
                        }
                    }
                }

                override fun onFailure(call: Call<FindTracksResponse>, t: Throwable) {
                    showProgressBar(false)
                    somethingWentWrong()
                }
            })*/
    }

    private fun hideKeyboard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}