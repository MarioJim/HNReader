package org.team4.hnreader.data.remote

class ItemTypeNotImplementedException(
    private val type: String,
    private val id: Int,
) : Exception() {
    override val message: String
        get() = "Item type \"$type\" not implemented (id $id)"
}
