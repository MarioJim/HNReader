package org.team4.hnreader.data.model

import java.io.Serializable

abstract class HNItem(
    open val author: String,
    open val created_at: Int,
    open val id: Int,
    open val type: String,
) : Serializable
