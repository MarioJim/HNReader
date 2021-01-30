package org.team4.hnreader.ui.adapters

import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.databinding.FragmentCommentBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils

class CommentViewHolder(
    val binding: FragmentCommentBinding,
    private val longClickListener: (Comment) -> Unit,
) : BindableViewHolder<Comment>(binding.root) {
    override fun bindTo(item: Comment) {
        val timeAgo = DateTimeUtils.timeAgo(item.created_at)
        binding.tvCommentInfo.text = "${item.author}, $timeAgo"
        binding.tvCommentText.text = TextUtils.fromHTML(item.text)
        // TODO: Long click listener doesn't work if link clicking is enabled
        // binding.tvCommentText.movementMethod = LinkMovementMethod.getInstance()

        binding.root.setOnLongClickListener {
            longClickListener(item)
            true
        }
    }
}
