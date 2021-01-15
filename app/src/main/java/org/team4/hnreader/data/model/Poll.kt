package org.team4.hnreader.data.model

import java.io.Serializable

const val POLL_TYPE = "poll"

data class Poll(
    override val by: String,
    override val id: Int,
    val parts: List<Int>,
    override val score: Int,
    val text: String?,
    override val time: Int,
    override val title: String,
) : Post(by, id, score, time, title, "poll"), Serializable
