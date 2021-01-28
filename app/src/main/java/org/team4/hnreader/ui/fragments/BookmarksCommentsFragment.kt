package org.team4.hnreader.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.remote.DeletedItemException
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.databinding.FragmentBookmarksCommentsBinding
import org.team4.hnreader.ui.adapters.CommentAdapter
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class BookmarksCommentsFragment : Fragment() {
    private var _binding: FragmentBookmarksCommentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var commentAdapter: CommentAdapter

    private var fromCache: Boolean = true
    private var commentsIds: ArrayList<Int> = ArrayList()
    private var commentsList: ArrayList<FlattenedComment> = ArrayList()
    private var lastLoadedComment: Int = 0

    private var isLoading: AtomicBoolean = AtomicBoolean(true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentBookmarksCommentsBinding.inflate(inflater, container, false)

        commentAdapter = CommentAdapter(
            activity as AppCompatActivity, null , commentsList)
        binding.rvBookmarksComments.adapter = commentAdapter
        val linearLayoutManager = LinearLayoutManager(binding.root.context)
        binding.rvBookmarksComments.layoutManager = linearLayoutManager
        binding.rvBookmarksComments.setHasFixedSize(true)
        binding.rvBookmarksComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewedItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val shouldLoadMoreComments = lastViewedItem + 7 >= commentsList.size
                if (shouldLoadMoreComments && isLoading.compareAndSet(false, true)) {
                    loadComments()
                }
            }
        })
        binding.srBookmarksComments.setOnRefreshListener { refreshPage() }

        refreshPage()

        return binding.root
    }

    private fun refreshPage() {
        fromCache = false
        FirestoreHelper.getInstance().getCommentsFromBookmarks {
            commentsIds.clear()
            commentsIds.plusAssign(it)
            commentsList.clear()
            lastLoadedComment = 0
            loadComments { binding.srBookmarksComments.isRefreshing = false }
        }
    }

    private fun loadComments(finishedCallback: () -> Unit = {}) {
        val numCommentsToAdd = min(
            NUM_COMMENTS_PER_LOAD_EVENT,
            commentsIds.size - lastLoadedComment,
        )
        if (numCommentsToAdd == 0) return
        val commentIdsToFetch = commentsIds.subList(
            lastLoadedComment,
            lastLoadedComment + numCommentsToAdd
        )
        ItemFinder.getInstance(binding.root.context).getCommentsFromIdsList(
            commentIdsToFetch,
            0,
            fromCache,
            { fetchedCommentList ->
                commentsList.addAll(fetchedCommentList)
                binding.rvBookmarksComments.post {
                    commentAdapter.notifyDataSetChanged()
                }
                lastLoadedComment += numCommentsToAdd
                isLoading.set(false)
                finishedCallback()
            },
            { displayError(it) }
        )
    }

    private fun displayError(error: VolleyError) {
        when (error.cause) {
            is DeletedItemException -> Log.e("DeletedItemException", "${error.message}")
            else -> {
                Log.e("volley error", error.message, error.cause)
                Toast.makeText(binding.root.context, "Error: " + error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val NUM_COMMENTS_PER_LOAD_EVENT = 5
    }
}