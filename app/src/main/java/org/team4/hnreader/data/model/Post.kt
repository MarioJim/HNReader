package org.team4.hnreader.data.model

import java.io.Serializable

abstract class Post(
    override val by: String,
    override val id: Int,
    open val score: Int,
    override val time: Int,
    open val title: String,
    override val type: String,
) : HNItem(by, id, time, type), Serializable
