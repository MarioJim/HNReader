package org.team4.hnreader.utils

import android.text.Html

object TextUtils {
    fun fromHTML(text: String): CharSequence =
        Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT).trim()
}
