package org.team4.hnreader.model

data class PollPart(
    override val by: String,
    override val id: Int,
    val poll: Int,
    val score: Int,
    val text: String,
    override val time: Int,
) : HNItem(by, id, ArrayList(), time, "pollopt")
