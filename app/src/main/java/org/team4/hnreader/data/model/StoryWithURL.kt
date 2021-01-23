package org.team4.hnreader.data.model

import org.json.JSONObject
import java.io.Serializable

data class StoryWithURL(
    override val author: String,
    override val created_at: Int,
    override val id: Int,
    override val numComments: Int,
    override val points: Int,
    override val title: String,
    val url: String,
) : Story(author, created_at, id, numComments, points, title), Serializable {
    companion object {
        fun fromJSONObject(jsonObject: JSONObject) = StoryWithURL(
            jsonObject.getString("author"),
            jsonObject.getInt("created_at_i"),
            Integer.parseInt(jsonObject.getString("objectID")),
            jsonObject.getInt("num_comments"),
            jsonObject.getInt("points"),
            jsonObject.getString("title"),
            jsonObject.getString("url")
        )
    }
}
