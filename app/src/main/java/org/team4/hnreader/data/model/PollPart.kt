package org.team4.hnreader.data.model

import java.io.Serializable

const val POLL_PART_TYPE = "pollopt"

data class PollPart(
    override val by: String,
    override val id: Int,
    val poll: Int,
    val score: Int,
    val text: String,
    override val time: Int,
) : HNItem(by, id, time, POLL_PART_TYPE), Serializable
