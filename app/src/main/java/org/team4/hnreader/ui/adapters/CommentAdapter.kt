package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.R
import org.team4.hnreader.data.model.*
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentCommentBinding
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils
import org.team4.hnreader.utils.URLUtils

class CommentAdapter(
    private val items: List<DisplayedItem>,
    private val showCommentMenuCallback: (comment: FlattenedComment) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun getItemViewType(position: Int) = when (items[position]) {
        is Story -> STORY_VIEW_TYPE
        is FlattenedComment -> COMMENT_VIEW_TYPE
        else -> throw Exception("ViewType for item ${items[position]} not implemented")
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            STORY_VIEW_TYPE -> {
                val binding = FragmentStoryBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false,
                )
                StoryViewHolder(binding)
            }
            COMMENT_VIEW_TYPE -> {
                val binding = FragmentCommentBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false,
                )
                CommentViewHolder(binding)
            }
            else -> throw Exception("ViewHolder for viewType $viewType not implemented")
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is StoryViewHolder) {
            onBindStoryViewHolder(viewHolder, items[position] as Story)
        } else if (viewHolder is CommentViewHolder) {
            onBindCommentViewHolder(viewHolder, items[position] as FlattenedComment)
        }
    }

    private fun onBindStoryViewHolder(viewHolder: StoryViewHolder, story: Story) {
        viewHolder.tvTitle.text = story.title
        viewHolder.tvUrl.text = when (story) {
            is StoryWithURL -> URLUtils.getDomain(story.url)
            is StoryWithText -> TextUtils.fromHTML(story.text)
            else -> ""
        }
        if (viewHolder.tvUrl.text.isEmpty())
            viewHolder.tvUrl.visibility = View.INVISIBLE
        val timeAgo = DateTimeUtils.timeAgo(story.created_at)
        viewHolder.tvInfo.text = "by ${story.author}, $timeAgo"
        viewHolder.tvVotes.text = "${story.points} points, ${story.numComments} comments"
        viewHolder.story = story

        viewHolder.saveBtn.visibility = if (firebaseAuth.currentUser == null) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

        FirestoreHelper.getInstance().checkIfStoryIsBookmark(story) {
            (viewHolder.saveBtn as MaterialButton).setIconTintResource(if (it) R.color.white else R.color.transparent)
        }
    }

    private fun onBindCommentViewHolder(viewHolder: CommentViewHolder, comment: FlattenedComment) {
        val timeAgo = DateTimeUtils.timeAgo(comment.created_at)
        viewHolder.tvCommentInfo.text = "${comment.author}, $timeAgo, depth ${comment.depth}"
        viewHolder.tvContent.text = TextUtils.fromHTML(comment.text)

        viewHolder.root.setOnLongClickListener {
            showCommentMenuCallback(comment)
            true
        }
    }

    override fun getItemCount() = items.size

    companion object {
        private const val STORY_VIEW_TYPE = 1
        private const val COMMENT_VIEW_TYPE = 2
    }
}
