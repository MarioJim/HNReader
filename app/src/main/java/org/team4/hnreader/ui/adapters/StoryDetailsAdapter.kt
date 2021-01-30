package org.team4.hnreader.ui.adapters

import android.content.res.Resources
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateMarginsRelative
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.R
import org.team4.hnreader.data.model.*
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentFlattenedCommentBinding
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils
import org.team4.hnreader.utils.URLUtils

class StoryDetailsAdapter(
    private val items: List<DisplayedItem>,
    private val showCommentMenuCallback: (comment: FlattenedComment) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun getItemViewType(position: Int) = when (items[position]) {
        is Story -> STORY_VIEW_TYPE
        is FlattenedComment -> FLAT_COMMENT_VIEW_TYPE
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
            FLAT_COMMENT_VIEW_TYPE -> {
                val binding = FragmentFlattenedCommentBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false,
                )
                FlattenedCommentViewHolder(binding)
            }
            else -> throw Exception("ViewHolder for viewType $viewType not implemented")
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is StoryViewHolder) {
            onBindStoryViewHolder(viewHolder, items[position] as Story)
        } else if (viewHolder is FlattenedCommentViewHolder) {
            onBindFlatCommentViewHolder(viewHolder, items[position] as FlattenedComment)
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

        if (firebaseAuth.currentUser == null) {
            viewHolder.saveBtn.visibility = View.GONE
        }

        FirestoreHelper.getInstance().checkIfStoryIsBookmark(story) {
            val tint = if (it) R.color.white else R.color.transparent
            (viewHolder.saveBtn as MaterialButton).setIconTintResource(tint)
        }
    }

    private fun onBindFlatCommentViewHolder(
        viewHolder: FlattenedCommentViewHolder,
        comment: FlattenedComment,
    ) {
        val newStartMargin = 20 * comment.depth * Resources.getSystem().displayMetrics.density
        val params = viewHolder.tvCommentInfo.layoutParams as ConstraintLayout.LayoutParams
        params.updateMarginsRelative(start = newStartMargin.toInt())

        val timeAgo = DateTimeUtils.timeAgo(comment.created_at)
        viewHolder.tvCommentInfo.text = "${comment.author}, $timeAgo"
        viewHolder.tvCommentText.text = TextUtils.fromHTML(comment.text)
        viewHolder.tvCommentText.movementMethod = LinkMovementMethod.getInstance()

        viewHolder.root.setOnLongClickListener {
            showCommentMenuCallback(comment)
            true
        }
    }

    override fun getItemCount() = items.size

    companion object {
        private const val STORY_VIEW_TYPE = 1
        private const val FLAT_COMMENT_VIEW_TYPE = 2
    }
}
