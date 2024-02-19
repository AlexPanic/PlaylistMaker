package com.example.playlistmaker.ui.common

import android.content.res.Resources
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.playlistmaker.R

class Helper {
    companion object {
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
            toolbar.setNavigationOnClickListener{
                activity.onBackPressed()
            }
        }
    }
}