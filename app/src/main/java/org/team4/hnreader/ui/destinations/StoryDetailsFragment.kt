package org.team4.hnreader.ui.destinations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import com.android.volley.VolleyError
import org.team4.hnreader.R
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.DeletedItemException
import org.team4.hnreader.databinding.FragmentStoryDetailsBinding
import org.team4.hnreader.ui.adapters.StoryDetailsAdapter
import org.team4.hnreader.ui.callbacks.ShowCommentMenu
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class StoryDetailsFragment : Fragment() {
    private var _binding: FragmentStoryDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyDetailsAdapter: StoryDetailsAdapter

    private lateinit var story: Story
    private val args: StoryDetailsFragmentArgs by navArgs()

    private var animationFinished: CompletableFuture<Unit> = CompletableFuture()
    private var fromCache: Boolean = true
    private var parentCommentIdsList: List<Int> = ArrayList()
    private var lastLoadedParentCommentIdx: Int = 0
    private var isLoading: AtomicBoolean = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater
            .from(context)
            .inflateTransition(R.transition.open_story_details)
            .addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {}
                override fun onTransitionCancel(transition: Transition) {}
                override fun onTransitionPause(transition: Transition) {}
                override fun onTransitionResume(transition: Transition) {}
                override fun onTransitionEnd(transition: Transition) {
                    animationFinished.complete(Unit)
                }
            })

        sharedElementReturnTransition = TransitionInflater
            .from(context)
            .inflateTransition(R.transition.open_story_details)

        story = args.story
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStoryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()

        parentCommentIdsList = story.kids

        storyDetailsAdapter = StoryDetailsAdapter(
            { startPostponedEnterTransition() },
            { comment ->
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                    (requireActivity() as ShowCommentMenu).showCommentMenu(comment)
            },
        )
        storyDetailsAdapter.clearAndSetStory(story) {}
        val linearLayoutManager = LinearLayoutManager(context)
        binding.rvDetails.apply {
            adapter = storyDetailsAdapter
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastViewedItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                    val shouldLoadMoreComments = lastViewedItem + 7 >= storyDetailsAdapter.itemCount
                    if (shouldLoadMoreComments && isLoading.compareAndSet(false, true)) {
                        loadComments()
                    }
                }
            })
        }
        binding.srDetails.setOnRefreshListener { refreshPage(story.id) }

        loadComments()
    }

    private fun refreshPage(storyId: Int) {
        fromCache = false
        ItemFinder.getInstance(context).getStory(
            storyId,
            fromCache,
            { fetchedStory ->
                isLoading.set(true)
                parentCommentIdsList = fetchedStory.kids
                storyDetailsAdapter.clearAndSetStory(fetchedStory) {
                    lastLoadedParentCommentIdx = 0
                    loadComments { binding.srDetails.isRefreshing = false }
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
        ItemFinder.getInstance(context).getCommentTreesFromIdsList(
            commentIdsToFetch,
            0,
            fromCache,
            { fetchedCommentList ->
                animationFinished.thenRun {
                    storyDetailsAdapter.extendList(fetchedCommentList) {
                        lastLoadedParentCommentIdx += numCommentsToAdd
                        isLoading.set(false)
                        finishedCallback()
                    }
                }
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
    }
}
