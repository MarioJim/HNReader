package org.team4.hnreader.ui.adapters

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.FragmentCommentBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils

class CommentAdapter(
    private val comments: List<Comment>,
    private val showCommentMenuCallback: (comment: FlattenedComment) -> Unit,
) : RecyclerView.Adapter<CommentViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = FragmentCommentBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false,
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        val timeAgo = DateTimeUtils.timeAgo(comment.created_at)
        viewHolder.tvCommentInfo.text = "${comment.author}, $timeAgo"
        viewHolder.tvCommentText.text = TextUtils.fromHTML(comment.text)
        viewHolder.tvCommentText.movementMethod = LinkMovementMethod.getInstance()

        viewHolder.root.setOnLongClickListener {
            val flattenedComment = FlattenedComment.fromComment(comment, 0)
            showCommentMenuCallback(flattenedComment)
            true
        }
    }

    override fun getItemCount() = comments.size
}
