package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.FragmentCommentBinding

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
        return CommentViewHolder(binding) {
            val flattenedComment = FlattenedComment.fromComment(it, 0)
            showCommentMenuCallback(flattenedComment)
        }
    }

    override fun onBindViewHolder(viewHolder: CommentViewHolder, position: Int) =
        viewHolder.bindTo(comments[position])

    override fun getItemCount() = comments.size
}
