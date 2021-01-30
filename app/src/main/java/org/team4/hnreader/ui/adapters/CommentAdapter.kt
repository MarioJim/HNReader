package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.FragmentCommentBinding

class CommentAdapter(
    private val showCommentMenuCallback: (comment: FlattenedComment) -> Unit,
) : ListAdapter<Comment, CommentViewHolder>(DIFF_CALLBACK) {
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
        viewHolder.bindTo(getItem(position))

    fun clearList(callback: Runnable) = submitList(emptyList(), callback)

    fun extendList(comments: List<Comment>, callback: Runnable) =
        submitList(currentList + comments, callback)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment) =
                areItemsTheSame(oldItem, newItem)
        }
    }
}
