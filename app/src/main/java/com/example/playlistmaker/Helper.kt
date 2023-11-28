package com.example.playlistmaker

import android.content.res.Resources
import android.util.TypedValue

class Helper {
    companion object {
        fun dpToPx(dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                Resources.getSystem().displayMetrics
            ).toInt()
        }
        fun millisToString(millis: Int): String {
            val sec = millis / 1000
            val mm = sec / 60
            val ss = "%02d".format(sec % 60)
            return "$mm:$ss"
        }
    }
}