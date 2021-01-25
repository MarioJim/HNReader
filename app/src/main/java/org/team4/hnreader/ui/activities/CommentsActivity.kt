package org.team4.hnreader.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.VolleyError
import org.team4.hnreader.R
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.ActivityCommentsBinding
import org.team4.hnreader.ui.adapters.CommentAdapter
import org.team4.hnreader.ui.fragments.StoryFragment
import java.util.concurrent.atomic.AtomicInteger

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private lateinit var commentsAdapter: CommentAdapter

    private lateinit var story: Story
    private lateinit var storyFragment: StoryFragment
    private var commentsList: ArrayList<FlattenedComment> = ArrayList()
    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Comments"

        val maybeStory = intent.getSerializableExtra("story") ?: {
            finish()
        }
        story = maybeStory as Story

        storyFragment = StoryFragment.newInstance(story)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.container, storyFragment)
            commit()
        }
        binding.goToAddCommentBtn.setOnClickListener {
            val intentToAddComment = Intent(this, AddCommentActivity::class.java).apply {
                putExtra("parent_id", story.id)
            }
            startActivity(intentToAddComment)
        }

        commentsAdapter = CommentAdapter(commentsList)
        binding.recyclerviewComments.adapter = commentsAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerviewComments.layoutManager = linearLayoutManager
        binding.recyclerviewComments.setHasFixedSize(true)
        isLoading = true
        loadMoreComments(0)
    }

    private fun loadMoreComments(startingPosition: Int) {
        val commentsToAdd = arrayOfNulls<List<FlattenedComment>>(story.kids.size)
        val commentTreesLeft = AtomicInteger(story.kids.size)
        for (commentTreeIdx in story.kids.indices) {
            ItemFinder.getInstance(this).getCommentTree(
                story.kids[commentTreeIdx],
                0,
                true,
                { childFlattenedCommentTree ->
                    commentsToAdd[commentTreeIdx] = childFlattenedCommentTree
                    if (commentTreesLeft.decrementAndGet() == 0) {
                        commentsList.addAll(commentsToAdd.filterNotNull().flatten())
                        commentsAdapter.notifyDataSetChanged()
                        isLoading = false
                    }
                },
                { error ->
                    displayError(error)
                    if (commentTreesLeft.decrementAndGet() == 0) {
                        commentsList.addAll(commentsToAdd.filterNotNull().flatten())
                        commentsAdapter.notifyDataSetChanged()
                        isLoading = false
                    }
                }
            )
        }
    }

    private fun displayError(error: VolleyError) {
        Log.e("volley error", error.message, error.cause)
        Toast.makeText(this, "Error: " + error.message, Toast.LENGTH_SHORT).show()
    }
}
