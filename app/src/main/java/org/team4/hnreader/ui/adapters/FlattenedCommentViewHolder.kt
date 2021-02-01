package org.team4.hnreader.ui.adapters

import android.content.res.Resources
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateMarginsRelative
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.FragmentFlattenedCommentBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils

class FlattenedCommentViewHolder(private val binding: FragmentFlattenedCommentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindTo(item: FlattenedComment, longClickListener: (FlattenedComment) -> Unit) {
        if (item.depth == 0) {
            binding.line.visibility =  View.GONE
        } else {
            val newStartMargin = 15 * (item.depth - 1) * Resources.getSystem().displayMetrics.density
            val params = binding.line.layoutParams as ConstraintLayout.LayoutParams
            params.updateMarginsRelative(start = newStartMargin.toInt())
        }

        val timeAgo = DateTimeUtils.timeAgo(item.created_at)
        binding.tvFlatCommentInfo.text = "${item.author}, $timeAgo"
        binding.tvFlatCommentText.text = TextUtils.fromHTML(item.text)
        // TODO: Long click listener doesn't work if link clicking is enabled
        // binding.tvFlatCommentText.movementMethod = LinkMovementMethod.getInstance()

        binding.root.setOnLongClickListener {
            longClickListener(item)
            true
        }
    }
}
