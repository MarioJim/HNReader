package org.team4.hnreader.data.model

const val STORY_TYPE = "story"

data class Story(
    override val by: String,
    override val id: Int,
    override val score: Int,
    override val time: Int,
    override val title: String,
    val url: String?,
) : Post(by, id, score, time, title, STORY_TYPE)
