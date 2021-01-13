package org.team4.hnreader.model

abstract class HNItem(
    open val by: String,
    open val id: Int,
    open val kids: List<Int>,
    open val time: Int,
    open val type: String,
)
