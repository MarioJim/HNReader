package org.team4.hnreader.data.model

import java.io.Serializable

const val COMMENT_TYPE = "comment"

data class Comment(
    override val by: String,
    override val id: Int,
    val text: String,
    override val time: Int,
) : HNItem(by, id, time, COMMENT_TYPE), Serializable
