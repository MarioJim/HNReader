package org.team4.hnreader.ui.adapters

import android.content.res.Resources
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.FragmentFlattenedCommentBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils

class FlattenedCommentViewHolder(private val binding: FragmentFlattenedCommentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindTo(comment: FlattenedComment, longClickListener: (FlattenedComment) -> Unit) {
        binding.line.visibility = if (comment.depth == 0) View.GONE else View.VISIBLE
        if (comment.depth > 0) {
            val newStartMargin =
                15 * (comment.depth - 1) * Resources.getSystem().displayMetrics.density
            val params = binding.line.layoutParams as ConstraintLayout.LayoutParams
            params.updateMargins(left = newStartMargin.toInt())
        }

        val timeAgo = DateTimeUtils.timeAgo(comment.created_at)
        binding.tvFlatCommentInfo.text = "${comment.author}, $timeAgo"
        binding.tvFlatCommentText.text = TextUtils.fromHTML(comment.text)
        // TODO: Long click listener doesn't work if link clicking is enabled
        // binding.tvFlatCommentText.movementMethod = LinkMovementMethod.getInstance()

        binding.root.setOnLongClickListener {
            longClickListener(comment)
            true
        }
    }
}
