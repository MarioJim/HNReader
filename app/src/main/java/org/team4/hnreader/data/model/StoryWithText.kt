package org.team4.hnreader.data.model

import android.content.ContentValues
import org.json.JSONObject
import org.team4.hnreader.data.local.ItemsTable
import java.io.Serializable

data class StoryWithText(
    override val author: String,
    override val created_at: Int,
    override val id: Int,
    override val kids: List<Int>,
    override val numComments: Int,
    override val points: Int,
    val text: String,
    override val title: String,
) : Story(author, created_at, id, kids, numComments, points, title), Serializable {
    companion object {
        fun fromJSONObject(jsonObject: JSONObject) = StoryWithText(
            jsonObject.getString("by"),
            jsonObject.getInt("time"),
            jsonObject.getInt("id"),
            kidsFromJSONObject(jsonObject),
            jsonObject.getInt("descendants"),
            jsonObject.getInt("score"),
            jsonObject.optString("text"),
            jsonObject.getString("title")
        )
    }

    override fun toItemsContentValues() = ContentValues().apply {
        put(ItemsTable.FIELD_AUTHOR, author)
        put(ItemsTable.FIELD_CREATED_AT, created_at)
        put(ItemsTable.FIELD_ID, id)
        put(ItemsTable.FIELD_NUM_COMMENTS, numComments)
        put(ItemsTable.FIELD_POINTS, points)
        put(ItemsTable.FIELD_TEXT, text)
        put(ItemsTable.FIELD_TITLE, title)
    }
}
