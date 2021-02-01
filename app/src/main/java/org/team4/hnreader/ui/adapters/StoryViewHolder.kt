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
import org.team4.hnreader.ui.callbacks.OpenStoryDetails
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.IntentUtils
import org.team4.hnreader.utils.TextUtils
import org.team4.hnreader.utils.URLUtils

class StoryViewHolder(private val binding: FragmentStoryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun listBindTo(story: Story, openStoryDetailsCallback: OpenStoryDetails) {
        if (story is StoryWithURL) {
            binding.tvUrl.text = URLUtils.getDomain(story.getUrl())
        } else {
            binding.tvUrl.visibility = View.GONE
        }

        binding.btnOpenLink.setOnClickListener {
            if (story is StoryWithURL) {
                IntentUtils.generateCustomTabsIntentBuilder(binding.root.context)
                    .build()
                    .launchUrl(binding.root.context, Uri.parse(story.getUrl()))
            } else {
                openStoryDetailsCallback(story)
            }
        }

        binding.btnOpenComments.setOnClickListener {
            openStoryDetailsCallback(story)
        }

        bindTo(story)
    }

    fun detailsBindTo(story: Story) {
        val urlText = when (story) {
            is StoryWithURL -> URLUtils.getDomain(story.getUrl())
            is StoryWithText -> TextUtils.fromHTML(story.text)
            else -> ""
        }
        if (urlText.isBlank()) {
            binding.tvUrl.visibility = View.GONE
        } else {
            binding.tvUrl.text = urlText
        }

        if (story is StoryWithURL) {
            binding.btnOpenLink.setOnClickListener {
                IntentUtils.generateCustomTabsIntentBuilder(binding.root.context)
                    .build()
                    .launchUrl(binding.root.context, Uri.parse(story.getUrl()))
            }
        }

        bindTo(story)
    }

    private fun bindTo(story: Story) {
        binding.tvTitle.text = story.title
        val timeAgo = DateTimeUtils.timeAgo(story.created_at)
        binding.tvInfo.text = "by ${story.author}, $timeAgo"
        binding.tvVotes.text = "${story.points} points, ${story.numComments} comments"

        binding.btnShare.setOnClickListener {
            val text = "Check this Hacker News post: ${story.getUrl()}"
            val shareIntent = IntentUtils.buildShareIntent(text)
            ContextCompat.startActivity(binding.root.context, shareIntent, null)
        }

        binding.btnBookmark.setOnClickListener {
            val firestoreHelper = FirestoreHelper.getInstance()
            firestoreHelper.checkIfStoryIsBookmark(story) { exist ->
                if (exist) {
                    firestoreHelper.removeStoryFromBookmarks(story) {
                        // TODO: Handle result
                    }
                    binding.btnBookmark.icon = getStarOutline()
                } else {
                    firestoreHelper.addStoryToBookmarks(story) {
                        // TODO: Handle result
                    }
                    binding.btnBookmark.icon = getStarFilled()
                }
            }
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            binding.btnBookmark.visibility = View.GONE
        }

        FirestoreHelper.getInstance().checkIfStoryIsBookmark(story) {
            binding.btnBookmark.icon = if (it) getStarFilled() else getStarOutline()
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
