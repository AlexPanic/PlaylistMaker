package com.example.playlistmaker.ui.common

import android.content.res.Resources
import android.util.TypedValue

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

    }
}