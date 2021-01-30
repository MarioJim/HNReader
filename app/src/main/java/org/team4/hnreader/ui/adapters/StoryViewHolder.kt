package org.team4.hnreader.ui.adapters

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.R
import org.team4.hnreader.ShareBroadcastReceiver
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithText
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.TextUtils
import org.team4.hnreader.utils.URLUtils

class StoryViewHolder(
    private val binding: FragmentStoryBinding,
    private val shouldDisplayText: Boolean,
    private val openCommentsRVCallback: (story: Story) -> Unit = {},
) : BindableViewHolder<Story>(binding.root) {
    override fun bindTo(item: Story) {
        binding.tvTitle.text = item.title
        binding.tvUrl.text = when (item) {
            is StoryWithURL -> URLUtils.getDomain(item.getUrl())
            is StoryWithText -> TextUtils.fromHTML(item.text)
            else -> {
                binding.tvUrl.visibility = View.GONE
                ""
            }
        }
        if (!shouldDisplayText && item is StoryWithText) {
            binding.tvUrl.visibility = View.GONE
        }
        val timeAgo = DateTimeUtils.timeAgo(item.created_at)
        binding.tvInfo.text = "by ${item.author}, $timeAgo"
        binding.tvVotes.text = "${item.points} points, ${item.numComments} comments"

        if (item is StoryWithURL) {
            binding.urlBtn.setOnClickListener {
                generateCustomTabsIntentBuilder()
                    .build()
                    .launchUrl(binding.root.context, Uri.parse(item.getUrl()))
            }
        }

        binding.commentsBtn.setOnClickListener {
            openCommentsRVCallback(item)
        }

        binding.shareBtn.setOnClickListener {
            val url = item.getUrl()
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check this Hacker News post: $url")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            ContextCompat.startActivity(binding.root.context, shareIntent, null)
        }

        binding.saveBtn.setOnClickListener {
            val firestoreHelper = FirestoreHelper.getInstance()
            firestoreHelper.checkIfStoryIsBookmark(item) { exist ->
                if (exist) {
                    firestoreHelper.removeStoryFromBookmarks(item) {
                        // TODO: Handle result
                    }
                    binding.saveBtn.setIconTintResource(R.color.transparent)
                } else {
                    firestoreHelper.addStoryToBookmarks(item) {
                        // TODO: Handle result
                    }
                    binding.saveBtn.setIconTintResource(R.color.white)
                }
            }
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            binding.saveBtn.visibility = View.GONE
        }

        FirestoreHelper.getInstance().checkIfStoryIsBookmark(item) {
            val tint = if (it) R.color.white else R.color.transparent
            binding.saveBtn.setIconTintResource(tint)
        }
    }

    private fun generateCustomTabsIntentBuilder() =
        CustomTabsIntent.Builder().apply {
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
            val shareIcon = BitmapFactory.decodeResource(
                binding.root.context.resources,
                android.R.drawable.ic_menu_share,
            )
            setActionButton(shareIcon, "Share via...", pendingIntent)

            val params = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(binding.root.context.getColor(R.color.purple_500))
                .build()
            setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)
        }
}
