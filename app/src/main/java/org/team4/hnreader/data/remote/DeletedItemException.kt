package org.team4.hnreader.data.remote

import org.json.JSONObject

class DeletedItemException(private val jsonObject: JSONObject) : Exception() {
    override val message: String
        get() = "Received deleted item: $jsonObject"
}
