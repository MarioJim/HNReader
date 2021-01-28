package org.team4.hnreader.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.DeletedItemException
import org.team4.hnreader.data.remote.FirestoreHelper
import org.team4.hnreader.data.remote.ItemTypeNotImplementedException
import org.team4.hnreader.databinding.FragmentBookmarksStoriesBinding
import org.team4.hnreader.ui.adapters.StoryAdapter
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class BookmarksStoriesFragment : Fragment() {
    private var _binding: FragmentBookmarksStoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyAdapter: StoryAdapter

    private var fromCache: Boolean = true
    private var storiesIds: ArrayList<Int> = ArrayList()
    private var storiesList: ArrayList<Story> = ArrayList()
    private var lastLoadedStory: Int = 0

    private var isLoading: AtomicBoolean = AtomicBoolean(true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentBookmarksStoriesBinding.inflate(inflater, container, false)

        storyAdapter = StoryAdapter(storiesList)
        binding.rvBookmarksStories.adapter = storyAdapter
        val linearLayoutManager = LinearLayoutManager(binding.root.context)
        binding.rvBookmarksStories.layoutManager = linearLayoutManager
        binding.rvBookmarksStories.setHasFixedSize(true)
        binding.rvBookmarksStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewedItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val shouldLoadMoreStories = lastViewedItem + 7 >= storiesList.size
                if (shouldLoadMoreStories && isLoading.compareAndSet(false, true)) {
                    loadStories()
                }
            }
        })
        binding.srBookmarksStories.setOnRefreshListener { refreshPage() }

        refreshPage()

        return binding.root
    }

    private fun refreshPage() {
        fromCache = false
        FirestoreHelper.getInstance().getStoriesFromBookmarks {
            storiesIds.clear()
            storiesIds.plusAssign(it)
            storiesList.clear()
            lastLoadedStory = 0
            loadStories { binding.srBookmarksStories.isRefreshing = false }
        }
    }

    private fun loadStories(finishedCallback: () -> Unit = {}) {
        val numStoriesToAdd = min(NUM_STORIES_PER_LOADING_EVENT, storiesIds.size - lastLoadedStory)
        if (numStoriesToAdd == 0) return
        val storyIdsToFetch = storiesIds.subList(
            lastLoadedStory,
            lastLoadedStory + numStoriesToAdd
        )
        ItemFinder.getInstance(binding.root.context).getStoriesFromIdsList(
            storyIdsToFetch,
            fromCache,
            { fetchedStories ->
                storiesList.addAll(fetchedStories)
                lastLoadedStory += numStoriesToAdd
                binding.rvBookmarksStories.post {
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
                Toast.makeText(activity, "Error: " + error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val NUM_STORIES_PER_LOADING_EVENT = 20
    }
}