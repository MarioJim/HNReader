package org.team4.hnreader.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithURL
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.utils.DateTimeUtils
import org.team4.hnreader.utils.URLUtils

class StoryFragment : Fragment() {
    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    private var story: Story =
        Story("User", 1610744647, -1, 123, 321, "Post Title")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            story = bundle.getSerializable(ARG_STORY) as Story
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)

        binding.tvTitle.text = story.title
        if (story is StoryWithURL)
            binding.tvUrl.text = URLUtils.getDomain((story as StoryWithURL).url)
        val timeAgo = DateTimeUtils.timeAgo(story.created_at)
        binding.tvInfo.text = "by ${story.author}, $timeAgo"
        refreshInfo(story.numComments)

        if (story is StoryWithURL) {
            binding.urlBtn.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse((story as StoryWithURL).url))
                startActivity(browserIntent)
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
            Toast.makeText(this.activity, "Saved item in bookmarks", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun refreshInfo(numComments: Int) {
        story.numComments = numComments
        binding.tvVotes.text = "${story.points} points, $numComments comments"
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
