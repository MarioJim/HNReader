package org.team4.hnreader.model

data class Poll(
    override val by: String,
    override val id: Int,
    override val kids: List<Int>,
    val parts: List<Int>,
    val score: Int,
    val text: String?,
    override val time: Int,
    val title: String,
) : HNItem(by, id, kids, time, "poll")
