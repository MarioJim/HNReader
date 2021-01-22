package org.team4.hnreader.data.model

import org.json.JSONObject
import java.io.Serializable

const val COMMENT_TYPE = "comment"

data class Comment(
    override val author: String,
    override val created_at: Int,
    override val id: Int,
    val parentId: Int,
    val text: String,
) : HNItem(author, created_at, id, COMMENT_TYPE), Serializable {
    companion object {
        fun fromJSONObject(jsonObject: JSONObject) = Comment(
            jsonObject.getString("author"),
            jsonObject.getInt("created_at_i"),
            Integer.parseInt(jsonObject.getString("objectID")),
            jsonObject.getInt("parent_id"),
            jsonObject.getString("comment_text")
        )
    }
}
