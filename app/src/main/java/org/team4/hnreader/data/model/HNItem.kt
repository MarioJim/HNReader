package org.team4.hnreader.data.model

import android.content.ContentValues
import org.json.JSONObject
import org.team4.hnreader.data.local.KidsTable
import java.io.Serializable

abstract class HNItem(
    open val author: String,
    open val created_at: Int,
    open val id: Int,
    open val kids: List<Int>,
    open val type: String,
) : Serializable {
    companion object {
        fun kidsFromJSONObject(jsonObject: JSONObject): List<Int> {
            return if (jsonObject.has("kids")) {
                val jsonArray = jsonObject.getJSONArray("kids")
                List(jsonArray.length()) { jsonArray.getInt(it) }
            } else {
                emptyList()
            }
        }
    }

    abstract fun toItemsContentValues(): ContentValues

    fun toKidsContentValues() = kids.map { kidID ->
        ContentValues().apply {
            put(KidsTable.FIELD_PARENT_ID, id)
            put(KidsTable.FIELD_KID_ID, kidID)
        }
    }
}
