package org.team4.hnreader.data.model

import android.content.ContentValues
import org.json.JSONObject
import org.team4.hnreader.data.local.ItemsTable
import java.io.Serializable

data class Comment(
    override val author: String,
    override val created_at: Int,
    override val id: Int,
    override val kids: List<Int>,
    val text: String,
) : Item(author, created_at, id, kids, TYPE), Serializable {
    companion object {
        const val TYPE = "comment"

        fun fromJSONObject(jsonObject: JSONObject) = Comment(
            jsonObject.getString("by"),
            jsonObject.getInt("time"),
            jsonObject.getInt("id"),
            kidsFromJSONObject(jsonObject),
            jsonObject.getString("text")
        )
    }

    override fun toItemsContentValues() = ContentValues().apply {
        put(ItemsTable.FIELD_AUTHOR, author)
        put(ItemsTable.FIELD_CREATED_AT, created_at)
        put(ItemsTable.FIELD_ID, id)
        put(ItemsTable.FIELD_TEXT, text)
        put(ItemsTable.FIELD_TYPE, type)
    }
}
