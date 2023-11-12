package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderAlertIcon: ImageView
    private lateinit var reSearchButton: Button
    private lateinit var adapter: TrackAdapter

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

    private fun showAlertIcon(whatIcon: String?) {
        reSearchButton.visibility = View.GONE
        when (whatIcon) {
            "nothing_found" -> {
                placeholderAlertIcon.setImageResource(R.drawable.nothing_found)
                placeholderAlertIcon.visibility = View.VISIBLE
            }
            "something_went_wrong" -> {
                placeholderAlertIcon.setImageResource(R.drawable.something_went_wrong)
                placeholderAlertIcon.visibility = View.VISIBLE
                reSearchButton.visibility = View.VISIBLE
            }
            else -> {
                placeholderAlertIcon.setImageResource(0)
                placeholderAlertIcon.visibility = View.GONE
            }
        }
    }

    private fun somethingWentWrong() {
        showMessage(getString(R.string.something_went_wrong), "")
        showAlertIcon("something_went_wrong")
        hideKeyboard()
    }

    private fun findItunes() {
        showAlertIcon(null)
        showMessage("Ищем...", "")
        itunesService.findTracks(inputEditText.text.toString())
            .enqueue(object : Callback<FindTracksResponse> {
                override fun onResponse(
                    call: Call<FindTracksResponse>,
                    response: Response<FindTracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            showAlertIcon(null)
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            } else {
                                showMessage(getString(R.string.nothing_found), "")
                                showAlertIcon("nothing_found")
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
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

        val btnBack = findViewById<ImageView>(R.id.go_back)
        btnBack.setOnClickListener{
            this.onBackPressed()
        }

        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderAlertIcon = findViewById(R.id.placeholderAlertIcon)

        inputEditText = findViewById<EditText>(R.id.search_input)
        inputEditText.setText(searchMask)

        // кнопка очистки поисковой маски
        val clearButton = findViewById<ImageView>(R.id.clear_search_input)
        clearButton.setOnClickListener {
            showMessage("","")
            inputEditText.setText("")
            tracks.clear()
            adapter.notifyDataSetChanged()
            hideKeyboard()
        }

        // кнопка перезапуска последнего поискового запроса
        reSearchButton = findViewById<Button>(R.id.btnReloadSearch)
        reSearchButton.setOnClickListener {
            if (searchMask.isNotEmpty()) {
                inputEditText.setText(searchMask)
                findItunes()
            }
        }

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
                    // empty
                }
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    findItunes()
                }
            }
            false
        }


        val trackList = findViewById<RecyclerView>(R.id.trackList)
        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = TrackAdapter(tracks)
        trackList.adapter = adapter

    }



}