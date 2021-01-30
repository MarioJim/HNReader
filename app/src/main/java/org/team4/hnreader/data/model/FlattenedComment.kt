package org.team4.hnreader.data.model

import java.io.Serializable

data class FlattenedComment(
    val author: String,
    val created_at: Int,
    val depth: Int,
    val id: Int,
    val text: String,
) : DisplayedItem, Serializable {
    companion object {
        fun fromComment(comment: Comment, depth: Int) = FlattenedComment(
            comment.author,
            comment.created_at,
            depth,
            comment.id,
            comment.text,
        )
    }

    fun getUrl() = "https://news.ycombinator.com/item?id=$id"

    override fun getItemId() = id

    override fun equalsDisplayedItem(other: DisplayedItem) = when (other) {
        is FlattenedComment -> id == other.id
        else -> false
    }
}
