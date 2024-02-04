package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.content.SharedPreferences
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
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.network.FindTracksResponse
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.R
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.enums.ApiResultIcons
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity(), TrackAdapter.ItemClickListener {

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)
    private var tracks = mutableListOf<Track>()
    private var tracksHistory = mutableListOf<Track>()
    private var history: TrackSearchHistory? = null
    private var sharedPref: SharedPreferences? = null
    private var sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private var searchTextLayout: TextInputLayout? = null
    private var placeholderMessage: TextView? = null
    private var placeholderAlertIcon: ImageView? = null
    private var reSearchButton: Button? = null
    private var adapter: TrackAdapter? = null
    private var adapterHistory: TrackAdapter? = null
    private var searchMask: String = ""
    private val searchRunnable = Runnable { searchRequest() }
    private var mainThreadHandler: Handler = Handler(Looper.getMainLooper())

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // инстанс настроек приложения
        sharedPref = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE)
        sharedPrefsListener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == TrackSearchHistory.sharedPrefKey) {
                    updateSearchHistory()
                }
            }
        sharedPref!!.registerOnSharedPreferenceChangeListener(sharedPrefsListener)

        // объект для управления историей
        history = TrackSearchHistory(sharedPref!!)

        // адаптер результатов поиска
        val trackList = findViewById<RecyclerView>(R.id.rvTracks)
        adapter = TrackAdapter(this, tracks, history)
        trackList.adapter = adapter

        updateSearchHistory()
        // адаптер истории поиска
        // так не работает adapterHistory.notifyDataSetChanged
        // поэтому полный перезагруз адаптера в updateSearchHistory()
        /*
        val searchHistoryTracksRecyclerView = findViewById<RecyclerView>(R.id.searchHistoryTracks)
        tracksHistory = history.getTracks()
        adapterHistory = TrackAdapter(this, tracksHistory, null)
        searchHistoryTracksRecyclerView.adapter = adapterHistory
         */

        // область сообщений
        placeholderMessage = findViewById(R.id.tvApiResponseMessage)
        placeholderAlertIcon = findViewById(R.id.ivApiResponseIcon)
        // поле с маской поиска
        searchTextLayout = findViewById(R.id.tilSearchMask)
        // установим сохраненное значение
        searchTextLayout!!.editText?.setText(searchMask)
        // очистка поискового запроса кнопкой
        searchTextLayout!!.setEndIconOnClickListener {
            searchTextLayout!!.editText?.setText("")
            showMessage("")
            showAlertIcon(ApiResultIcons.EMPTY)
            tracks.clear()
            adapter!!.notifyDataSetChanged()
            updateSearchHistory()
            hideKeyboard()
        }

        // покажем историю поиска при выполнении условий
        searchTextLayout!!.editText?.setOnFocusChangeListener { _, hasFocus ->
            searchHistoryVisibility(
                hasFocus
                        && searchTextLayout!!.editText?.text.toString().isEmpty()
                        && tracksHistory.isNotEmpty()
            )
        }

        // очистка истории поиска
        val btnSearchHistoryClear = findViewById<Button>(R.id.btTrackHistoryClear)
        btnSearchHistoryClear.setOnClickListener {
            if (history!=null) {
                history!!.clearTracks()
            }
        }

        // кнопка перезапуска последнего поискового запроса
        reSearchButton = findViewById(R.id.btSearchReload)
        reSearchButton!!.setOnClickListener {
            if (searchMask.isNotEmpty()) {
                searchTextLayout!!.editText?.setText(searchMask)
                searchRequest()
            }
        }

        // следим за изменением в поисковой строке
        searchTextLayout!!.editText!!.doOnTextChanged { text, _, _, _ ->
            searchMask = text.toString()
            searchHistoryVisibility(searchTextLayout!!.editText!!.hasFocus() && searchMask.isEmpty())
            searchDebounce()
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(position: Int, fromHistory: Boolean) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(
            Track.INTENT_EXTRA_ID,
            if (fromHistory) tracksHistory[position] else tracks[position]
        )
        startActivity(intent)
    }

    private fun searchHistoryVisibility(visible: Boolean) {
        val searchHistoryView = findViewById<ViewGroup>(R.id.llSearchHistory)
        searchHistoryView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updateSearchHistory() {
        if (history != null) {
            tracksHistory = history!!.getTracks()
            if (tracksHistory.isEmpty()) {
                searchHistoryVisibility(false)
            } else {
                // здесь такая команда почему-то (???) не работает (список истории не меняется)
                // adapterHistory.notifyDataSetChanged()
                // поэтому полный перезагруз адаптера (так работает)
                val searchHistoryTracksRecyclerView =
                    findViewById<RecyclerView>(R.id.rvTracksHistory)
                adapterHistory = TrackAdapter(this, tracksHistory, null)
                searchHistoryTracksRecyclerView.adapter = adapterHistory
            }
        }
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderMessage!!.isVisible = true
            tracks.clear()
            adapter!!.notifyDataSetChanged()
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
        tracks.clear()
        adapter!!.notifyDataSetChanged()
        showProgressBar(true)
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
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter!!.notifyDataSetChanged()
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
            })
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