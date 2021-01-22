package org.team4.hnreader.data.model

import java.io.Serializable

const val STORY_TYPE = "story"

data class Story(
    override val by: String,
    override val id: Int,
    var numComments: Int,
    val score: Int,
    override val time: Int,
    val title: String,
    val url: String,
) : HNItem(by, id,  time, STORY_TYPE), Serializable
