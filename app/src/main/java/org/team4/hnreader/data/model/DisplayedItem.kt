package org.team4.hnreader.data.model

interface DisplayedItem {
    fun getItemId(): Int
    fun equalsDisplayedItem(other: DisplayedItem): Boolean
}
