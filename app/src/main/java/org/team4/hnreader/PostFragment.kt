package org.team4.hnreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

private const val POST_TITLE = "title"
private const val POST_URL = "url"
private const val POST_USER = "username"
private const val POST_VOTES = "votes"
private const val POST_NUM_COMMENTS = "comments"
private const val POST_DATE = "date"

class PostFragment : Fragment() {
    private var postTitle: String? = null
    private var postURL: String? = "https://news.ycombinator.com"
    private var postUser: String? = null
    private var postVotes: Int? = null
    private var postNumComments: Int? = null
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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)
        val urlBtn = view.findViewById<ConstraintLayout>(R.id.urlBtn)
        val commentsBtn = view.findViewById<ConstraintLayout>(R.id.commentsBtn)
        val shareButton = view.findViewById<Button>(R.id.shareBtn)
        val saveButton = view.findViewById<Button>(R.id.saveBtn)

        urlBtn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(postURL))
            startActivity(browserIntent)
        }

        commentsBtn.setOnClickListener {
            val intentToComments = Intent(this.activity, CommentsActivity::class.java)
            startActivity(intentToComments)
        }

        shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, postURL)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        saveButton.setOnClickListener {
            Toast.makeText(this.activity, "Saved item in bookmarks", Toast.LENGTH_SHORT).show()
        }

        return view
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