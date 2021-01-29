package org.team4.hnreader.ui.fragments

import org.team4.hnreader.R
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.ShareBroadcastReceiver
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.URLUtils

class StoryFragment : Fragment() {
    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    private var story: Story = StoryWithURL(
        "User",
        1610744647,
        -1,
        emptyList(),
        123,
        321,
        "Post Title",
        "https://news.ycombinator.com/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            story = bundle.getSerializable(ARG_STORY) as Story
        }

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)

        binding.tvTitle.text = story.title
        if (story is StoryWithURL)
            binding.tvUrl.text = URLUtils.getDomain((story as StoryWithURL).url)
        val timeAgo = DateTimeUtils.timeAgo(story.created_at)
        binding.tvInfo.text = "by ${story.author}, $timeAgo"
        binding.tvVotes.text = "${story.points} points, ${story.numComments} comments"

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

        binding.shareBtn.setOnClickListener {
            val url = if (story is StoryWithURL) {
                (story as StoryWithURL).url
            } else {
                "https://news.ycombinator.com/item?id=${story.id}"
            }
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check this Hacker News post: $url")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.saveBtn.setOnClickListener {
            FirestoreHelper.getInstance().addStoryToBookmarks(story!!) {
                Toast.makeText(binding.root.context, it.second, Toast.LENGTH_SHORT).show()
            }
        }

        binding.saveBtn.visibility = if (firebaseAuth.currentUser == null) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_STORY = "story"

        @JvmStatic
        fun newInstance(story: Story) =
            StoryFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_STORY, story)
                }
            }
    }
}
