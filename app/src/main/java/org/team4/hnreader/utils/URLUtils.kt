package org.team4.hnreader.utils

import java.net.URI

class URLUtils {
    companion object {
        fun getDomain(url: String): String {
            val domain = URI(url).host
            return if (domain.startsWith("www.")) {
                domain.substring(4)
            } else {
                domain
            }
        }
    }
}
