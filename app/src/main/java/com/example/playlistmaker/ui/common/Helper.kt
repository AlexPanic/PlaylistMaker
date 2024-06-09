package com.example.playlistmaker.ui.common

import android.content.res.Resources
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.playlistmaker.R
import kotlin.math.abs

class Helper {
    companion object {

        const val COVER_RADIUS = 8f

        fun dpToPx(dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                Resources.getSystem().displayMetrics
            ).toInt()
        }

        fun setToolbar(activity: AppCompatActivity) {
            val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
            activity.setSupportActionBar(toolbar)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            activity.supportActionBar?.setDisplayShowHomeEnabled(true)
            toolbar.setNavigationOnClickListener {
                activity.onBackPressedDispatcher.onBackPressed()
            }
        }

        fun getWordFormDependingOnNumber(
            number: Int,
            wordFor1: String,
            wordFor2: String,
            wordFor5: String
        ): String {
            val n = abs(number) % 100
            val n1 = abs(n) % 10
            return if (n in 11..19)
                wordFor5
            else if (n1 in 2..4)
                wordFor2
            else if (n1 == 1)
                wordFor1
            else wordFor5
        }
    }
}