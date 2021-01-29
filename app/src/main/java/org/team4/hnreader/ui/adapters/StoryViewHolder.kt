package org.team4.hnreader.ui.adapters

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.team4.hnreader.R
import org.team4.hnreader.ShareBroadcastReceiver
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentStoryBinding

class StoryViewHolder(
    binding: FragmentStoryBinding,
    private val openCommentsRVCallback: (story: Story) -> Unit = {},
) : RecyclerView.ViewHolder(binding.root) {
    val tvTitle = binding.tvTitle
    val tvInfo = binding.tvInfo
    val tvUrl = binding.tvUrl
    val tvVotes = binding.tvVotes
    val saveBtn = binding.saveBtn
    var story: Story? = null

    init {
        binding.urlBtn.setOnClickListener {
            if (story is StoryWithURL) {
                val url = (story as StoryWithURL).url
                val builder = CustomTabsIntent.Builder()

                val shareIcon = BitmapFactory.decodeResource(
                    binding.root.context.resources,
                    android.R.drawable.ic_menu_share,
                )
                val shareIntent = Intent(
                    binding.root.context,
                    ShareBroadcastReceiver::class.java,
                )
                val pendingIntent = PendingIntent.getBroadcast(
                    binding.root.context,
                    0,
                    shareIntent,
                    0,
                )
                builder.setActionButton(shareIcon, "Share via...", pendingIntent)

                val params = CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(binding.root.context.getColor(R.color.purple_500))
                    .build()
                builder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)

                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(binding.root.context, Uri.parse(url))
            }
        }
        binding.commentsBtn.setOnClickListener { openCommentsRVCallback(story!!) }
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
                val firestoreHelper = FirestoreHelper.getInstance()
                firestoreHelper.checkIfStoryIsBookmark(story!!) { exist ->
                    if (exist) {
                        firestoreHelper.removeStoryFromBookmarks(story!!) {
                            // TODO: Handle result
                        }
                        (binding.saveBtn as MaterialButton).setIconTintResource(R.color.transparent)
                    } else {
                        firestoreHelper.addStoryToBookmarks(story!!) {
                            // TODO: Handle result
                        }
                        (binding.saveBtn as MaterialButton).setIconTintResource(R.color.white)
                    }
                }
            }
        }
    }
}
