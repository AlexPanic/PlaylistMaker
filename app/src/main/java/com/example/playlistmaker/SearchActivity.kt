package com.example.playlistmaker

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)
    private var tracks = mutableListOf<Track>()
    private var tracksHistory = mutableListOf<Track>()
    private lateinit var history: TrackSearchHistory
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var searchTextLayout: TextInputLayout
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderAlertIcon: ImageView
    private lateinit var reSearchButton: Button
    private lateinit var adapter: TrackAdapter
    private lateinit var adapterHistory: TrackAdapter

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    enum class ResultsIcon(val drawableId: Int) {
        NOTHING_FOUND(R.drawable.nothing_found),
        SOMETHING_WENT_WRONG(R.drawable.something_went_wrong),
        EMPTY(0)
    }

    private fun showAlertIcon(icon: ResultsIcon) {
        placeholderAlertIcon.setImageResource(icon.drawableId)
        when (icon) {
            ResultsIcon.SOMETHING_WENT_WRONG -> {
                reSearchButton.visibility = View.VISIBLE
            }

            else -> {
                reSearchButton.visibility = View.GONE
            }
        }
    }

    private fun somethingWentWrong() {
        showMessage(getString(R.string.something_went_wrong), "")
        showAlertIcon(ResultsIcon.SOMETHING_WENT_WRONG)
        hideKeyboard()
    }

    private fun showProgressBar(visible: Boolean) {
        val bar = findViewById<ProgressBar>(R.id.progressBar)
        bar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun findItunes() {
        showAlertIcon(ResultsIcon.EMPTY)
        showProgressBar(true)
        itunesService.findTracks(searchTextLayout.editText?.text.toString())
            .enqueue(object : Callback<FindTracksResponse> {
                override fun onResponse(
                    call: Call<FindTracksResponse>,
                    response: Response<FindTracksResponse>
                ) {
                    showProgressBar(false)
                    when (response.code()) {
                        200 -> {
                            showAlertIcon(ResultsIcon.EMPTY)
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            } else {
                                showMessage(getString(R.string.nothing_found), "")
                                showAlertIcon(ResultsIcon.NOTHING_FOUND)
                            }
                        }

                        else -> {
                            somethingWentWrong()
                        }
                    }
                }

                override fun onFailure(call: Call<FindTracksResponse>, t: Throwable) {
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

    private var searchMask: String = ""

    private companion object {
        var SEARCH_STRING = "SEARCH_STRING"
    }

    private fun searchHistoryVisibility(visible: Boolean) {
        val searchHistoryView = findViewById<ViewGroup>(R.id.searchHistory)
        searchHistoryView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updateSearchHistory() {
        tracksHistory = history.getTracks()
        if (tracksHistory.isEmpty()) {
            searchHistoryVisibility(false)
        } else {
            // здесь такая команда почему-то (???) не работает (список истории не меняется)
            // adapterHistory.notifyDataSetChanged()
            // поэтому полный перезагруз адаптера (так работает)
            val searchHistoryTracksRecyclerView =
                findViewById<RecyclerView>(R.id.searchHistoryTracks)
            adapterHistory = TrackAdapter(tracksHistory, null)
            searchHistoryTracksRecyclerView.adapter = adapterHistory
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // инстанс настроек приложения
        sharedPref = getSharedPreferences(getString(R.string.file_preferences), MODE_PRIVATE)
        sharedPrefsListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
                if (key == TrackSearchHistory.sharedPrefKey) {
                    updateSearchHistory()
                }
            }
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPrefsListener)

        // объект для управления историей
        history = TrackSearchHistory(sharedPref)

        // адаптер результатов поиска
        val trackList = findViewById<RecyclerView>(R.id.trackList)
        adapter = TrackAdapter(tracks, history)
        trackList.adapter = adapter

        updateSearchHistory()
        // адаптер истории поиска
        // так не работает adapterHistory.notifyDataSetChanged
        // поэтому полный перезагруз адаптера в updateSearchHistory()
        /*
        val searchHistoryTracksRecyclerView = findViewById<RecyclerView>(R.id.searchHistoryTracks)
        tracksHistory = history.getTracks()
        adapterHistory = TrackAdapter(tracksHistory, null)
        searchHistoryTracksRecyclerView.adapter = adapterHistory
         */

        // область сообщений
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderAlertIcon = findViewById(R.id.placeholderAlertIcon)
        // поле с маской поиска
        searchTextLayout = findViewById<TextInputLayout>(R.id.search_input_layout)
        // установим сохраненное значение
        searchTextLayout.editText?.setText(searchMask)
        // очистка поискового запроса кнопкой
        searchTextLayout.setEndIconOnClickListener {
            searchTextLayout.editText?.setText("")
            showMessage("", "")
            showAlertIcon(ResultsIcon.EMPTY)
            tracks.clear()
            adapter.notifyDataSetChanged()
            updateSearchHistory()
            hideKeyboard()
        }

        // покажем историю поиска при выполнении условий
        searchTextLayout.editText?.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryVisibility(
                hasFocus
                        && searchTextLayout.editText?.text.toString().isEmpty()
                        && tracksHistory.isNotEmpty()
            )
        }

        // очистка истории поиска
        val btnSearchHistoryClear = findViewById<Button>(R.id.btnSearchHistoryClear)
        btnSearchHistoryClear.setOnClickListener {
            history.clearTracks()
        }

        // кнопка перезапуска последнего поискового запроса
        reSearchButton = findViewById<Button>(R.id.btnReloadSearch)
        reSearchButton.setOnClickListener {
            if (searchMask.isNotEmpty()) {
                searchTextLayout.editText?.setText(searchMask)
                findItunes()
            }
        }

        // следим за изменением в поисковой строке
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    // empty
                } else {
                    val input = s.toString()
                    searchMask = input
                }
                searchHistoryVisibility(searchTextLayout.editText!!.hasFocus() && s!!.isEmpty())
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchTextLayout.editText?.addTextChangedListener(simpleTextWatcher)
        // запускаем поиск на нажатие кнопки Enter (Done)
        searchTextLayout.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchTextLayout.editText?.text.toString().isNotEmpty()) {
                    findItunes()
                } else {
                    showMessage("empty mask", "empty mask")
                }
            }
            false
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


}