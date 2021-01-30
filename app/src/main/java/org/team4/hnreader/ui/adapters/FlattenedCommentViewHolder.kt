package org.team4.hnreader.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.databinding.FragmentFlattenedCommentBinding

class FlattenedCommentViewHolder(binding: FragmentFlattenedCommentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val root = binding.root
    val tvCommentInfo = binding.tvFlatCommentInfo
    val tvCommentText = binding.tvFlatCommentText
}
