package org.team4.hnreader.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.DisplayedItem
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.DeletedItemException
import org.team4.hnreader.databinding.FragmentCommentsRecyclerViewBinding
import org.team4.hnreader.ui.adapters.CommentAdapter
import org.team4.hnreader.ui.callbacks.ShowCommentMenu
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class CommentsRecyclerViewFragment : Fragment() {
    private var _binding: FragmentCommentsRecyclerViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var commentsAdapter: CommentAdapter

    private lateinit var story: Story

    private var fromCache: Boolean = true
    private var parentCommentIdsList: List<Int> = ArrayList()
    private var itemsList: ArrayList<DisplayedItem> = ArrayList()
    private var lastLoadedParentCommentIdx: Int = 0

    // Start as true for the initial comment loading
    private var isLoading: AtomicBoolean = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            story = it.getSerializable(ARG_STORY) as Story
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommentsRecyclerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentCommentIdsList = story.kids

        itemsList.add(story)
        commentsAdapter = CommentAdapter(itemsList) { comment ->
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                (requireActivity() as ShowCommentMenu).showCommentMenu(comment)
        }
        binding.recyclerviewComments.adapter = commentsAdapter
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerviewComments.layoutManager = linearLayoutManager
        binding.recyclerviewComments.setHasFixedSize(true)
        binding.recyclerviewComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewedItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val shouldLoadMoreComments = lastViewedItem + 7 >= itemsList.size
                if (shouldLoadMoreComments && isLoading.compareAndSet(false, true)) {
                    loadComments()
                }
            }
        })
        binding.srComments.setOnRefreshListener { refreshPage(story.id) }

        loadComments()

    }

    private fun refreshPage(storyId: Int) {
        fromCache = false
        ItemFinder.getInstance(context).getStory(
            storyId,
            fromCache,
            {
                itemsList.clear()
                itemsList.add(it)
                parentCommentIdsList = it.kids
                lastLoadedParentCommentIdx = 0
                binding.recyclerviewComments.adapter = commentsAdapter
                loadComments { binding.srComments.isRefreshing = false }
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
        ItemFinder.getInstance(context).getCommentTreesFromIdsList(
            commentIdsToFetch,
            0,
            fromCache,
            { fetchedCommentList ->
                itemsList.addAll(fetchedCommentList)
                binding.recyclerviewComments.post {
                    commentsAdapter.notifyDataSetChanged()
                }
                lastLoadedParentCommentIdx += numCommentsToAdd
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
                Toast.makeText(context, "Error: " + error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val NUM_COMMENTS_PER_LOAD_EVENT = 5

        private const val ARG_STORY = "story"

        @JvmStatic
        fun newInstance(story: Story) =
            CommentsRecyclerViewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_STORY, story)
                }
            }
    }
}
