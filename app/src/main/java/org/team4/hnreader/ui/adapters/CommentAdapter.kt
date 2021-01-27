package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithText
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.databinding.FragmentCommentBinding
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.ui.fragments.CommentOptionsBottomSheet
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils
import org.team4.hnreader.utils.URLUtils

class CommentAdapter(
    private val activity: AppCompatActivity,
    private val story: Story,
    private val comments: List<FlattenedComment>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun getItemViewType(position: Int) =
        if (position == 0) STORY_VIEW_TYPE else COMMENT_VIEW_TYPE

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            STORY_VIEW_TYPE -> {
                val binding = FragmentStoryBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false,
                )
                StoryViewHolder(binding, false)
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
            onBindStoryViewHolder(viewHolder)
        } else if (viewHolder is CommentViewHolder) {
            onBindCommentViewHolder(viewHolder, comments[position - 1])
        }
    }

    private fun onBindStoryViewHolder(viewHolder: StoryViewHolder) {
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

        viewHolder.btnSave.visibility = if (firebaseAuth.currentUser == null) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private fun onBindCommentViewHolder(viewHolder: CommentViewHolder, comment: FlattenedComment) {
        val timeAgo = DateTimeUtils.timeAgo(comment.created_at)
        viewHolder.tvCommentInfo.text = "${comment.author}, $timeAgo, depth ${comment.depth}"
        viewHolder.tvContent.text = TextUtils.fromHTML(comment.text)

        viewHolder.root.setOnLongClickListener {
            CommentOptionsBottomSheet(comment).show(
                activity.supportFragmentManager,
                CommentOptionsBottomSheet.TAG,
            )
            true
        }
    }

    override fun getItemCount() = comments.size + 1

    companion object {
        private const val STORY_VIEW_TYPE = 1
        private const val COMMENT_VIEW_TYPE = 2
    }
}
