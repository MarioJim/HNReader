package org.team4.hnreader.utils

import android.text.format.DateUtils

class DateTimeUtils {
    companion object {
        fun timeAgo(unixTime: Int): String =
            DateUtils.getRelativeTimeSpanString(unixTime.toLong() * 1000).toString()
    }
}
