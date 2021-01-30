package org.team4.hnreader.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.databinding.FragmentCommentBinding

class CommentViewHolder(binding: FragmentCommentBinding) : RecyclerView.ViewHolder(binding.root) {
    val root = binding.root
    val tvCommentInfo = binding.tvCommentInfo
    val tvCommentText = binding.tvCommentText
}
