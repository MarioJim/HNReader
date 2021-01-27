package org.team4.hnreader.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.DeletedItemException
import org.team4.hnreader.databinding.ActivityCommentsBinding
import org.team4.hnreader.ui.adapters.CommentAdapter
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private lateinit var commentsAdapter: CommentAdapter

    private var fromCache: Boolean = true
    private var parentCommentIdsList: List<Int> = ArrayList()
    private var commentsList: ArrayList<FlattenedComment> = ArrayList()
    private var lastLoadedParentCommentIdx: Int = 0

    // Start as true for the initial comment loading
    private var isLoading: AtomicBoolean = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Comments"

        val maybeStory = intent.getSerializableExtra(ARG_STORY) ?: {
            finish()
        }
        val story = maybeStory as Story
        parentCommentIdsList = story.kids

        binding.srComments.setOnRefreshListener { refreshPage(story.id) }

        commentsAdapter = CommentAdapter(this, story, commentsList)
        binding.recyclerviewComments.adapter = commentsAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerviewComments.layoutManager = linearLayoutManager
        binding.recyclerviewComments.setHasFixedSize(true)
        binding.recyclerviewComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewedItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val shouldLoadMoreComments = lastViewedItem + 7 >= commentsList.size
                if (shouldLoadMoreComments && isLoading.compareAndSet(false, true)) {
                    loadComments()
                }
            }
        })
        loadComments()
    }

    private fun refreshPage(storyId: Int) {
        fromCache = false

        ItemFinder.getInstance(this).getStory(
            storyId,
            fromCache,
            {
                parentCommentIdsList = it.kids
                commentsList = ArrayList()
                lastLoadedParentCommentIdx = 0
                commentsAdapter = CommentAdapter(this, it, commentsList)
                binding.recyclerviewComments.adapter = commentsAdapter
                loadComments {
                    binding.srComments.isRefreshing = false
                }
            },
            { displayError(it) },
        )
    }

    private fun loadComments(finishedCallback: () -> Unit = {}) {
        val numCommentsToAdd = min(
            NUM_COMMENTS_PER_LOAD_EVENT,
            parentCommentIdsList.size - lastLoadedParentCommentIdx,
        )
        if (numCommentsToAdd == 0) return
        val commentIdsToFetch = parentCommentIdsList.subList(
            lastLoadedParentCommentIdx,
            lastLoadedParentCommentIdx + numCommentsToAdd
        )
        ItemFinder.getInstance(this).fetchCommentsFromIdsList(
            commentIdsToFetch,
            0,
            fromCache,
            { fetchedCommentList ->
                commentsList.addAll(fetchedCommentList)
                binding.recyclerviewComments.post {
                    commentsAdapter.notifyDataSetChanged()
                }
                lastLoadedParentCommentIdx += numCommentsToAdd
                isLoading.set(false)
                finishedCallback()
            },
            { error ->
                when (error.cause) {
                    is DeletedItemException -> Log.e("DeletedItemException", "${error.message}")
                    else -> displayError(error)
                }
            }
        )
    }

    private fun displayError(error: VolleyError) {
        Log.e("volley error", error.message, error.cause)
        Toast.makeText(this, "Error: " + error.message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ARG_STORY = "story"

        private const val NUM_COMMENTS_PER_LOAD_EVENT = 5
    }
}
