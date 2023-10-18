package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_MASK = "SEARCH_MASK"
        const val SEARCH_MASK_DEF = ""
    }

    private var searchMask: String = SEARCH_MASK_DEF

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_MASK, searchMask)
    }
    override fun onRestoreInstanceState(outState: Bundle) {
        super.onRestoreInstanceState(outState)
        searchMask = outState.getString(SEARCH_MASK, SEARCH_MASK_DEF)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        findViewById<ImageView>(R.id.go_back).setOnClickListener{
            this.onBackPressed()
        }

        val inputEditText = findViewById<EditText>(R.id.search_input)
        inputEditText.setText(searchMask)

        val clearButton = findViewById<ImageView>(R.id.clear_search_input)
        clearButton.setOnClickListener {
            inputEditText.setText("")
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
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}