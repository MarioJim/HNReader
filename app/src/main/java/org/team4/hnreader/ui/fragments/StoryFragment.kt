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
import org.team4.hnreader.databinding.FragmentStoryBinding
import org.team4.hnreader.utils.DateTimeConversions
import java.net.URI

private const val ARG_STORY = "story"

class StoryFragment : Fragment() {
    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    private var story: Story =
        Story("User", 1610744647, -1, 123, 321, "", "Post Title", "https://news.ycombinator.com/")

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
        binding.tvUrl.text = getDomainName(story.url)
        val timeAgo = DateTimeConversions.timeAgo(story.created_at)
        binding.tvInfo.text = "by ${story.author}, $timeAgo"
        refreshInfo(story.numComments)

        binding.urlBtn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(story.url))
            startActivity(browserIntent)
        }

        binding.shareBtn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check this Hacker News post: ${story.url}")
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

    private fun getDomainName(url: String): String {
        val domain: String = URI(url).host
        return if (domain.startsWith("www.")) domain.substring(4) else domain
    }

    fun refreshInfo(numComments: Int) {
        story.numComments = numComments
        binding.tvVotes.text = "${story.points} points, $numComments comments"
    }

    companion object {
        @JvmStatic
        fun newInstance(story: Story) =
            StoryFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_STORY, story)
                }
            }
    }
}
