package org.team4.hnreader.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import org.team4.hnreader.R
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.ActivityCommentsBinding
import org.team4.hnreader.ui.adapters.CommentAdapter
import org.team4.hnreader.ui.fragments.StoryFragment
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private lateinit var commentsAdapter: CommentAdapter

    private lateinit var story: Story
    private lateinit var storyFragment: StoryFragment
    private var commentsList: ArrayList<FlattenedComment> = ArrayList()
    private var lastLoadedTree: Int = 0
    // Start as true for the initial comment loading
    private var isLoading: Boolean = true

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
        binding.recyclerviewComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewedItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (!isLoading && lastViewedItem + 7 >= commentsList.size) {
                    loadMoreComments(false)
                    isLoading = true
                }
            }
        })
        loadMoreComments(true)
    }

    private fun loadMoreComments(initialLoad: Boolean) {
        var numCommentsToAdd =
            if (initialLoad) NUM_STARTING_COMMENTS else NUM_COMMENTS_PER_LOAD_EVENT
        numCommentsToAdd = min(numCommentsToAdd, story.kids.size - lastLoadedTree)
        val commentsToAdd = arrayOfNulls<List<FlattenedComment>>(numCommentsToAdd)
        val commentTreesLeft = AtomicInteger(numCommentsToAdd)
        val finishedFetching = {
            commentsList.addAll(commentsToAdd.filterNotNull().flatten())
            commentsAdapter.notifyDataSetChanged()
            lastLoadedTree += numCommentsToAdd
            isLoading = false
        }
        for (commentTreeIdx in 0 until numCommentsToAdd) {
            ItemFinder.getInstance(this).getCommentTree(
                story.kids[lastLoadedTree + commentTreeIdx],
                0,
                true,
                { childFlattenedCommentTree ->
                    commentsToAdd[commentTreeIdx] = childFlattenedCommentTree
                    if (commentTreesLeft.decrementAndGet() == 0) finishedFetching()
                },
                { error ->
                    displayError(error)
                    if (commentTreesLeft.decrementAndGet() == 0) finishedFetching()
                }
            )
        }
    }

    private fun displayError(error: VolleyError) {
        Log.e("volley error", error.message, error.cause)
        Toast.makeText(this, "Error: " + error.message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val NUM_STARTING_COMMENTS = 10
        private const val NUM_COMMENTS_PER_LOAD_EVENT = 5
    }
}
