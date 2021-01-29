package org.team4.hnreader.ui.adapters

import org.team4.hnreader.R
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.ShareBroadcastReceiver
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.ui.activities.CommentsActivity


class StoryViewHolder(
    binding: FragmentStoryBinding,
    private val shouldOpenComments: Boolean,
) : RecyclerView.ViewHolder(binding.root) {
    val container = binding.storyFragmentContainer
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
                val builder : CustomTabsIntent.Builder = CustomTabsIntent.Builder()

                val shareIcon =
                    BitmapFactory.decodeResource(binding.root.context.resources,
                        android.R.drawable.ic_menu_share)

                val shareIntent = Intent(
                    binding.root.context, ShareBroadcastReceiver::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(binding.root.context,
                        0,
                        shareIntent,
                        0)

                builder.setActionButton(shareIcon, "Share via...", pendingIntent)

                val params = CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(binding.root.context.getColor(R.color.purple_500))
                    .build()
                builder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)

                val customTabsIntent : CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(binding.root.context, Uri.parse(url))
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
            if (story != null) {
                FirestoreHelper.getInstance().addStoryToBookmarks(story!!) {
                    Toast.makeText(binding.root.context, it.second, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
