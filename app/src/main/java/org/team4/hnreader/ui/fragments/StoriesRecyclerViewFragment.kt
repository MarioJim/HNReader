package org.team4.hnreader.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.DeletedItemException
import org.team4.hnreader.data.remote.ItemTypeNotImplementedException
import org.team4.hnreader.databinding.FragmentStoriesRecyclerViewBinding
import org.team4.hnreader.ui.adapters.StoryAdapter
import org.team4.hnreader.ui.callbacks.StoryRecyclerViewCallbacks
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class StoriesRecyclerViewFragment : Fragment() {
    private var _binding: FragmentStoriesRecyclerViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var callbacksProvider: StoryRecyclerViewCallbacks

    private var showingStories: List<Story>? = null
    private var fromCache: Boolean = true
    private var storiesIds: List<Int> = ArrayList()
    private var lastLoadedStory: Int = 0
    private var isLoading: AtomicBoolean = AtomicBoolean(true)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStoriesRecyclerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (parentFragment is StoryRecyclerViewCallbacks) {
            callbacksProvider = parentFragment as StoryRecyclerViewCallbacks
        } else {
            throw Exception("StoriesRecyclerViewFragment created in a fragment that doesn't extend StoryIdsSourceAndClickHandler")
        }

        storyAdapter = StoryAdapter { story ->
            callbacksProvider.openStoryDetails(story)
        }
        binding.recyclerviewStories.adapter = storyAdapter
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerviewStories.layoutManager = linearLayoutManager
        binding.recyclerviewStories.setHasFixedSize(true)
        binding.recyclerviewStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewedItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val shouldLoadMoreStories = lastViewedItem + 7 >= storyAdapter.itemCount
                if (shouldLoadMoreStories && isLoading.compareAndSet(false, true)) {
                    loadStories()
                }
            }
        })

        binding.srStories.setOnRefreshListener { refreshPage() }

        callbacksProvider.fetchStoryIds(
            {
                storiesIds = it
                loadStories()
            },
            { displayError(it) })
    }


    override fun onResume() {
        super.onResume()
        // TODO: Remove workaround for saving storyAdapter's inner list
        if (storyAdapter.currentList.size == 0)
            showingStories?.let { storyAdapter.extendList(it) {} }
    }

    override fun onPause() {
        super.onPause()
        // TODO: Remove workaround for saving storyAdapter's inner list
        showingStories = storyAdapter.currentList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshPage() {
        fromCache = false
        callbacksProvider.fetchStoryIds(
            {
                storiesIds = it
                isLoading.set(true)
                lastLoadedStory = 0
                storyAdapter.resetList {
                    loadStories {
                        binding.srStories.isRefreshing = false
                    }
                }
            },
            { displayError(it) })
    }

    private fun loadStories(finishedCallback: () -> Unit = {}) {
        val numStoriesToAdd = min(NUM_STORIES_PER_LOADING_EVENT, storiesIds.size - lastLoadedStory)
        if (numStoriesToAdd == 0) return
        val storyIdsToFetch = storiesIds.subList(
            lastLoadedStory,
            lastLoadedStory + numStoriesToAdd
        )
        ItemFinder.getInstance(context).getStoriesFromIdsList(
            storyIdsToFetch,
            fromCache,
            { fetchedStories ->
                storyAdapter.extendList(fetchedStories) {
                    lastLoadedStory += numStoriesToAdd
                    isLoading.set(false)
                    finishedCallback()
                }
            },
            { displayError(it) }
        )
    }

    private fun displayError(error: Exception) {
        when (error.cause) {
            is DeletedItemException -> Log.e("DeletedItemException", "${error.message}")
            is ItemTypeNotImplementedException -> Log.e("ItemTypeNotImplementedException",
                "${error.message}")
            else -> {
                Log.e("volley error", error.message, error.cause)
                Toast.makeText(context, "Error: " + error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val NUM_STORIES_PER_LOADING_EVENT = 20
    }
}
