package org.team4.hnreader.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.ApiRequestQueue
import org.team4.hnreader.databinding.ActivityMainBinding
import org.team4.hnreader.ui.activities.BookmarksActivity
import org.team4.hnreader.ui.adapters.StoryAdapter
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private var storiesIds: ArrayList<Int> = ArrayList()
    private var storiesList: ArrayList<Story> = ArrayList()
    // Don't load stories until storiesIds is filled
    private var isLoading: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.bookmarksBtn.setOnClickListener {
            val intentToBookmarks = Intent(this, BookmarksActivity::class.java)
            startActivity(intentToBookmarks)
        }
        storyAdapter = StoryAdapter(storiesList)
        binding.recyclerviewStories.adapter = storyAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerviewStories.layoutManager = linearLayoutManager
        binding.recyclerviewStories.setHasFixedSize(true)
        binding.recyclerviewStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewedItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (!isLoading && lastViewedItem + 7 >= storiesList.size) {
                    loadMoreStories()
                    isLoading = true
                }
            }
        })

        // Load story ids
        ApiRequestQueue.getInstance().fetchTopStoriesIds(
            {
                storiesIds.plusAssign(it)
                loadMoreStories()
            },
            { displayError(it) })
    }

    private fun loadMoreStories() {
        val numStoriesToAdd = min(NUM_STORIES_PER_LOADING_EVENT, storiesIds.size - storiesList.size)
        if (numStoriesToAdd == 0) return
        val storiesToAdd = arrayOfNulls<Story>(numStoriesToAdd)
        val storiesToAddCounter = AtomicInteger(numStoriesToAdd)
        val finishedFetching = {
            storiesList.addAll(storiesToAdd.filterNotNull().toTypedArray())
            storyAdapter.notifyDataSetChanged()
            isLoading = false
        }
        for (i in storiesList.size until storiesList.size + numStoriesToAdd) {
            ItemFinder.getInstance(this).getStory(
                storiesIds[i],
                true,
                { story ->
                    storiesToAdd[i - storiesList.size] = story
                    if (storiesToAddCounter.decrementAndGet() == 0) finishedFetching()
                },
                {
                    displayError(it)
                    if (storiesToAddCounter.decrementAndGet() == 0) finishedFetching()
                }
            )
        }
    }

    private fun displayError(error: VolleyError) {
        Log.e("volley error", error.message, error.cause)
        Toast.makeText(this, "Error: " + error.message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val NUM_STORIES_PER_LOADING_EVENT = 20
    }
}
