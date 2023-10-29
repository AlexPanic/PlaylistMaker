package com.example.playlistmaker

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

class Helper {
    companion object {
        fun dpToPx(context: Context, dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                Resources.getSystem().displayMetrics
            ).toInt()
        }
    }
}