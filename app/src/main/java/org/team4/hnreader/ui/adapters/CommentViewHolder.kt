package org.team4.hnreader.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.databinding.FragmentCommentBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils

class CommentViewHolder(private val binding: FragmentCommentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindTo(comment: Comment, longClickListener: (Comment) -> Unit) {
        val timeAgo = DateTimeUtils.timeAgo(comment.created_at)
        binding.tvCommentInfo.text = "${comment.author}, $timeAgo"
        binding.tvCommentText.text = TextUtils.fromHTML(comment.text)
        // TODO: Long click listener doesn't work if link clicking is enabled
        // binding.tvCommentText.movementMethod = LinkMovementMethod.getInstance()

        binding.root.setOnLongClickListener {
            longClickListener(comment)
            true
        }
    }
}
