package org.team4.hnreader.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BindableViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindTo(item: T)
}
