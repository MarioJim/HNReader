package org.team4.hnreader.ui.adapters

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.ui.activities.CommentsActivity

class StoryViewHolder(
    binding: FragmentStoryBinding,
    private val shouldOpenComments: Boolean,
) : RecyclerView.ViewHolder(binding.root) {
    val tvTitle = binding.tvTitle
    val tvInfo = binding.tvInfo
    val tvUrl = binding.tvUrl
    val tvVotes = binding.tvVotes
    val btnSave = binding.saveBtn
    var story: Story? = null

    init {
        binding.urlBtn.setOnClickListener {
            if (story is StoryWithURL) {
                val url = (story as StoryWithURL).url
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                ContextCompat.startActivity(binding.root.context, browserIntent, null)
            }
        }
        binding.commentsBtn.setOnClickListener {
            if (shouldOpenComments) {
                val intentToComments =
                    Intent(binding.root.context, CommentsActivity::class.java).apply {
                        putExtra(CommentsActivity.ARG_STORY, story)
                    }
                ContextCompat.startActivity(binding.root.context, intentToComments, null)
            }
        }
        binding.shareBtn.setOnClickListener {
            val url = if (story is StoryWithURL) {
                (story as StoryWithURL).url
            } else {
                "https://news.ycombinator.com/item?id=${story?.id}"
            }
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check this Hacker News post: $url")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            ContextCompat.startActivity(binding.root.context, shareIntent, null)
        }
        binding.saveBtn.setOnClickListener {
            // TODO: Save in bookmarks
            Toast.makeText(binding.root.context, "Saved item in bookmarks", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
