package org.team4.hnreader.data.model

import org.json.JSONObject
import java.io.Serializable

abstract class Story(
    override val author: String,
    override val created_at: Int,
    override val id: Int,
    open val numComments: Int,
    open val points: Int,
    open val title: String,
) : HNItem(author, created_at, id, TYPE), Serializable {
    companion object {
        const val TYPE = "story"

        fun fromJSONObject(jsonObject: JSONObject): Story {
            return if (jsonObject.has("url")) {
                StoryWithURL.fromJSONObject(jsonObject)
            } else {
                StoryWithText.fromJSONObject(jsonObject)
            }
        }
    }
}
