package org.team4.hnreader.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.team4.hnreader.databinding.FragmentPostBinding
import org.team4.hnreader.ui.activities.CommentsActivity
import java.net.URI

private const val POST_TITLE = "title"
private const val POST_URL = "url"
private const val POST_USER = "username"
private const val POST_VOTES = "votes"
private const val POST_NUM_COMMENTS = "comments"
private const val POST_DATE = "date"

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var postTitle: String? = "Post Title"
    private var postURL: String? = "https://news.ycombinator.com"
    private var postUser: String? = "User"
    private var postVotes: Int = 0
    private var postNumComments: Int = 0
    private var postDate: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postTitle = it.getString(POST_TITLE)
            postURL = it.getString(POST_URL)
            postUser = it.getString(POST_USER)
            postVotes = it.getInt(POST_VOTES)
            postNumComments = it.getInt(POST_NUM_COMMENTS)
            postDate = it.getInt(POST_DATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        binding.tvTitle.text = "$postTitle"
        binding.tvUrl.text = getDomainName("$postURL")
        binding.tvInfo.text = "by $postUser, 3 hours ago"
        binding.tvVotes.text = "$postVotes points, $postNumComments comments"

        binding.urlBtn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(postURL))
            startActivity(browserIntent)
        }

        binding.commentsBtn.setOnClickListener {
            val intentToComments = Intent(this.activity, CommentsActivity::class.java)
            startActivity(intentToComments)
        }

        binding.shareBtn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, postURL)
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

    private fun getDomainName(url: String): String? {
        val domain: String = URI(url).host
        return if (domain.startsWith("www.")) domain.substring(4) else domain
    }

    companion object {
        @JvmStatic
        fun newInstance(
            title: String,
            url: String,
            user: String,
            votes: Int,
            numComments: Int,
            date: Int
        ) =
            PostFragment().apply {
                arguments = Bundle().apply {
                    putString(POST_TITLE, title)
                    putString(POST_URL, url)
                    putString(POST_USER, user)
                    putInt(POST_VOTES, votes)
                    putInt(POST_NUM_COMMENTS, numComments)
                    putInt(POST_DATE, date)
                }
            }
    }
}
