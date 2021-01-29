package org.team4.hnreader.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.R
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.URLUtils

class StoryAdapter(
    private val stories: List<Story>,
    private val openCommentsRVCallback: (story: Story) -> Unit,
) : RecyclerView.Adapter<StoryViewHolder>() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            FragmentStoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return StoryViewHolder(binding, openCommentsRVCallback)
    }

    override fun onBindViewHolder(viewHolder: StoryViewHolder, position: Int) {
        val story = stories[position]
        viewHolder.tvTitle.text = story.title
        if (story is StoryWithURL) {
            viewHolder.tvUrl.visibility = View.VISIBLE
            viewHolder.tvUrl.text = URLUtils.getDomain(story.url)
        } else {
            viewHolder.tvUrl.visibility = View.INVISIBLE
        }
        val timeAgo = DateTimeUtils.timeAgo(story.created_at)
        viewHolder.tvInfo.text = "by ${story.author}, $timeAgo"
        viewHolder.tvVotes.text = "${story.points} points, ${story.numComments} comments"
        viewHolder.story = story

        if (firebaseAuth.currentUser == null) {
            viewHolder.saveBtn.visibility = View.INVISIBLE
        }

        FirestoreHelper.getInstance().checkIfStoryIsBookmark(story) {
            (viewHolder.saveBtn as MaterialButton).setIconTintResource(if (it) R.color.white else R.color.transparent)
        }
    }

    override fun getItemCount() = stories.size
}
