package org.team4.hnreader.model

data class Story(
    override val by: String,
    override val id: Int,
    override val kids: List<Int>,
    val score: Int,
    override val time: Int,
    val title: String,
    val url: String?,
) : HNItem(by, id, kids, time, "story")
