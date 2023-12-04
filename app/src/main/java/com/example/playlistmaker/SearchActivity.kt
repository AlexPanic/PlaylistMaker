package com.example.playlistmaker

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
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

    private fun findItunes() {
        showAlertIcon(ResultsIcon.EMPTY)
        showMessage("Ищем...", "")
        itunesService.findTracks(searchTextLayout.editText?.text.toString())
            .enqueue(object : Callback<FindTracksResponse> {
                override fun onResponse(
                    call: Call<FindTracksResponse>,
                    response: Response<FindTracksResponse>
                ) {
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

    private companion object {
        const val SEARCH_MASK = "SEARCH_MASK"
    }

    private var searchMask: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_MASK, searchMask)
    }

    override fun onRestoreInstanceState(outState: Bundle) {
        super.onRestoreInstanceState(outState)
        searchMask = outState.getString(SEARCH_MASK, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPrefs = App.getSharedPreferences()
        val sharedPrefsListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs, key ->
                Log.d("mine", "// Global History changed!")
            }

        /*val sharedPrefs = getSharedPreferences(getString(R.string.file_preferences), MODE_PRIVATE)
        val sharedPrefsListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs, key ->
                /*if (key == TrackSearchHistory.searchHistoryKey) {
                    tracksHistory = TrackSearchHistory.getTracks()
                    adapterHistory.notifyDataSetChanged()
                    Log.d("mine", "History count: " + tracksHistory.size.toString())
                }*/
                Log.d("mine", "// History changed!")
            }*/
        sharedPrefs.registerOnSharedPreferenceChangeListener(sharedPrefsListener)
        val TrackSearchHistory = TrackSearchHistory(sharedPrefs)

        // адаптер истории поиска
        val searchHistoryTracksRecyclerView = findViewById<RecyclerView>(R.id.searchHistoryTracks)
        tracksHistory = TrackSearchHistory.getTracks()
        adapterHistory = TrackAdapter(tracksHistory, false)
        searchHistoryTracksRecyclerView.adapter = adapterHistory




        /*val sharedPreferences = App.getSharedPreferences()
        val sharedPrefsListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == TrackSearchHistory.searchHistoryKey) {
                    tracksHistory = TrackSearchHistory.getTracks()
                    adapterHistory.notifyDataSetChanged()
                    Log.d("mine", "History count: " + tracksHistory.size.toString())
                }
            }
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPrefsListener)*/

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
            adapterHistory.notifyDataSetChanged()
            hideKeyboard()
        }

        // покажем историю поиска при выполнении условий
        val searchHistoryView = findViewById<ViewGroup>(R.id.searchHistory)
        searchTextLayout.editText?.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryView.visibility =
                if (hasFocus
                    && searchTextLayout.editText?.text.toString().isEmpty()
                    && tracksHistory.isNotEmpty()
                ) View.VISIBLE else View.GONE
        }

        // очистка истории поиска
        val btnSearchHistoryClear = findViewById<Button>(R.id.btnSearchHistoryClear)
        btnSearchHistoryClear.setOnClickListener {
            TrackSearchHistory.clearTracks()
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
                searchHistoryView.visibility =
                    if (searchTextLayout.editText!!.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
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

        // адаптер результатов поиска
        val trackList = findViewById<RecyclerView>(R.id.trackList)
        adapter = TrackAdapter(tracks, true)
        trackList.adapter = adapter

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