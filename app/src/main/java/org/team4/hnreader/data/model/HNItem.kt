package org.team4.hnreader.data.model

abstract class HNItem(
    open val by: String,
    open val id: Int,
    open val time: Int,
    open val type: String,
)
