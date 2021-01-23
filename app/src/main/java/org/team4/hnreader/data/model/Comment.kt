package org.team4.hnreader.data.model

import android.content.ContentValues
import org.json.JSONObject
import org.team4.hnreader.data.local.ItemsTable
import org.team4.hnreader.data.local.KidsTable
import java.io.Serializable

data class Comment(
    override val author: String,
    override val created_at: Int,
    override val id: Int,
    val parentId: Int,
    val text: String,
) : HNItem(author, created_at, id, COMMENT_TYPE), Serializable {
    companion object {
        const val COMMENT_TYPE = "comment"

        fun fromJSONObject(jsonObject: JSONObject) = Comment(
            jsonObject.getString("author"),
            jsonObject.getInt("created_at_i"),
            Integer.parseInt(jsonObject.getString("objectID")),
            jsonObject.getInt("parent_id"),
            jsonObject.getString("comment_text")
        )
    }

    fun toItemsContentValues() = ContentValues().apply {
        put(ItemsTable.FIELD_AUTHOR, author)
        put(ItemsTable.FIELD_ID, id)
        put(ItemsTable.FIELD_TEXT, text)
        put(ItemsTable.FIELD_CREATED_AT, created_at)
        put(ItemsTable.FIELD_TYPE, type)
    }

    fun toKidsContentValues() = ContentValues().apply {
        put(KidsTable.FIELD_PARENT_ID, parentId)
        put(KidsTable.FIELD_KID_ID, id)
    }
}
