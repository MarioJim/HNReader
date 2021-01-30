package org.team4.hnreader.ui.adapters

import android.content.res.Resources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateMarginsRelative
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.FragmentFlattenedCommentBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils

class FlattenedCommentViewHolder(
    val binding: FragmentFlattenedCommentBinding,
    private val longClickListener: (FlattenedComment) -> Unit,
) : BindableViewHolder<FlattenedComment>(binding.root) {
    override fun bindTo(item: FlattenedComment) {
        val newStartMargin = 20 * item.depth * Resources.getSystem().displayMetrics.density
        val params = binding.tvFlatCommentInfo.layoutParams as ConstraintLayout.LayoutParams
        params.updateMarginsRelative(start = newStartMargin.toInt())

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
