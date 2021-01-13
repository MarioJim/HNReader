package org.team4.hnreader.model

data class Comment(
    override val by: String,
    override val id: Int,
    override val kids: List<Int>,
    val parent: Int,
    val text: String,
    override val time: Int,
) : HNItem(by, id, kids, time,  "comment")
