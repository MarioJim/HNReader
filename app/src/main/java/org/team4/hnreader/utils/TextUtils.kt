package org.team4.hnreader.utils

import android.text.Html
import android.text.Spanned

class TextUtils {
    companion object {
        fun fromHTML(text: String): Spanned = Html.fromHtml(
            text,
            Html.FROM_HTML_MODE_COMPACT or Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
        )
    }
}
