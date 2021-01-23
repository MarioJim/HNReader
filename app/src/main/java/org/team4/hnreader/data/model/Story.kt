package org.team4.hnreader.data.model

import org.json.JSONObject
import java.io.Serializable

open class Story(
    override val author: String,
    override val created_at: Int,
    override val id: Int,
    open var numComments: Int,
    open val points: Int,
    open val title: String,
) : HNItem(author, created_at, id, STORY_TYPE), Serializable {
    companion object {
        const val STORY_TYPE = "story"

        fun fromJSONObject(jsonObject: JSONObject): Story {
            return if (jsonObject.isNull("url")) {
                StoryWithText.fromJSONObject(jsonObject)
            } else {
                StoryWithURL.fromJSONObject(jsonObject)
            }
        }
    }
}
