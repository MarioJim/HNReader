package org.team4.hnreader.ui.adapters

import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.R
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithText
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.IntentUtils
import org.team4.hnreader.utils.TextUtils
import org.team4.hnreader.utils.URLUtils

class StoryViewHolder(private val binding: FragmentStoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindTo(
        item: Story,
        shouldDisplayText: Boolean,
        openStoryDetailsCallback: (story: Story) -> Unit = {},
    ) {
        binding.tvTitle.text = item.title
        binding.tvUrl.text = when {
            item is StoryWithURL -> URLUtils.getDomain(item.getUrl())
            shouldDisplayText -> TextUtils.fromHTML((item as StoryWithText).text)
            else -> {
                binding.tvUrl.visibility = View.GONE
                ""
            }
        }
        val timeAgo = DateTimeUtils.timeAgo(item.created_at)
        binding.tvInfo.text = "by ${item.author}, $timeAgo"
        binding.tvVotes.text = "${item.points} points, ${item.numComments} comments"

        binding.urlBtn.setOnClickListener {
            if (item is StoryWithURL) {
                IntentUtils.generateCustomTabsIntentBuilder(binding.root.context)
                    .build()
                    .launchUrl(binding.root.context, Uri.parse(item.getUrl()))
            } else {
                openStoryDetailsCallback(item)
            }
        }

        binding.commentsBtn.setOnClickListener {
            openStoryDetailsCallback(item)
        }

        binding.shareBtn.setOnClickListener {
            val text = "Check this Hacker News post: ${item.getUrl()}"
            val shareIntent = IntentUtils.buildShareIntent(text)
            ContextCompat.startActivity(binding.root.context, shareIntent, null)
        }

        binding.saveBtn.setOnClickListener {
            val firestoreHelper = FirestoreHelper.getInstance()
            firestoreHelper.checkIfStoryIsBookmark(item) { exist ->
                if (exist) {
                    firestoreHelper.removeStoryFromBookmarks(item) {
                        // TODO: Handle result
                    }
                    binding.saveBtn.icon = getStarOutline()
                } else {
                    firestoreHelper.addStoryToBookmarks(item) {
                        // TODO: Handle result
                    }
                    binding.saveBtn.icon = getStarFilled()
                }
            }
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            binding.saveBtn.visibility = View.GONE
        }

        FirestoreHelper.getInstance().checkIfStoryIsBookmark(item) {
            binding.saveBtn.icon = if (it) getStarFilled() else getStarOutline()
        }
    }

    private fun getStarOutline() = ResourcesCompat.getDrawable(
        binding.root.context.resources,
        R.drawable.ic_star_outline,
        binding.root.context.theme,
    )

    private fun getStarFilled() = ResourcesCompat.getDrawable(
        binding.root.context.resources,
        R.drawable.ic_star_filled,
        binding.root.context.theme,
    )
}
