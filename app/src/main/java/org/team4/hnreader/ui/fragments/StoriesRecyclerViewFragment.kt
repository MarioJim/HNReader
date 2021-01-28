package org.team4.hnreader.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.ApiRequestQueue
import org.team4.hnreader.data.remote.DeletedItemException
import org.team4.hnreader.data.remote.ItemTypeNotImplementedException
import org.team4.hnreader.databinding.FragmentStoriesRecyclerViewBinding
import org.team4.hnreader.ui.adapters.StoryAdapter
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class StoriesRecyclerViewFragment : Fragment() {
    private var _binding: FragmentStoriesRecyclerViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyAdapter: StoryAdapter

    private var fromCache: Boolean = true
    private var storiesIds: ArrayList<Int> = ArrayList()
    private var storiesList: ArrayList<Story> = ArrayList()
    private var lastLoadedStory: Int = 0

    // Don't load stories until storiesIds is filled
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

        storyAdapter = StoryAdapter(storiesList) { story ->
            val directions = StoriesRecyclerViewFragmentDirections
                .actionStoriesRecyclerViewFragmentToCommentsRecyclerViewFragment(story)
            findNavController().navigate(directions)
        }
        binding.recyclerviewStories.adapter = storyAdapter
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerviewStories.layoutManager = linearLayoutManager
        binding.recyclerviewStories.setHasFixedSize(true)
        binding.recyclerviewStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewedItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val shouldLoadMoreStories = lastViewedItem + 7 >= storiesList.size
                if (shouldLoadMoreStories && isLoading.compareAndSet(false, true)) {
                    loadStories()
                }
            }
        })

        binding.srStories.setOnRefreshListener { refreshPage() }

        ApiRequestQueue.getInstance().fetchTopStoriesIds(
            {
                storiesIds.plusAssign(it)
                loadStories()
            },
            { displayError(it) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateItems() = storyAdapter.notifyDataSetChanged()

    private fun refreshPage() {
        fromCache = false
        ApiRequestQueue.getInstance().fetchTopStoriesIds(
            {
                storiesIds.clear()
                storiesIds.plusAssign(it)
                storiesList.clear()
                lastLoadedStory = 0
                loadStories { binding.srStories.isRefreshing = false }
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
                storiesList.addAll(fetchedStories)
                lastLoadedStory += numStoriesToAdd
                binding.recyclerviewStories.post {
                    storyAdapter.notifyDataSetChanged()
                }
                isLoading.set(false)
                finishedCallback()
            },
            { displayError(it) }
        )
    }

    private fun displayError(error: VolleyError) {
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
