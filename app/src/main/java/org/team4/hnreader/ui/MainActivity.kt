package org.team4.hnreader.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.data.ItemFinder
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.ApiRequestQueue
import org.team4.hnreader.data.remote.DeletedItemException
import org.team4.hnreader.data.remote.ItemTypeNotImplementedException
import org.team4.hnreader.databinding.ActivityMainBinding
import org.team4.hnreader.ui.activities.BookmarksActivity
import org.team4.hnreader.ui.activities.LoginActivity
import org.team4.hnreader.ui.adapters.StoryAdapter
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storyAdapter: StoryAdapter

    private var fromCache: Boolean = true
    private var storiesIds: ArrayList<Int> = ArrayList()
    private var storiesList: ArrayList<Story> = ArrayList()
    private var lastLoadedStory: Int = 0

    // Don't load stories until storiesIds is filled
    private var isLoading: AtomicBoolean = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

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
                val shouldLoadMoreStories = lastViewedItem + 7 >= storiesList.size
                if (shouldLoadMoreStories && isLoading.compareAndSet(false, true)) {
                    loadStories()
                }
            }
        })
        binding.srStories.setOnRefreshListener { refreshPage() }

        binding.loginBtn.setOnClickListener {
            val intentToLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentToLogin)
        }
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkIfSignedIn()
        }

        // Load story ids
        ApiRequestQueue.getInstance().fetchTopStoriesIds(
            {
                storiesIds.plusAssign(it)
                loadStories()
            },
            { displayError(it) })

        checkIfSignedIn()
    }

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
        ItemFinder.getInstance(this).getStoriesFromIdsList(
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
                Toast.makeText(this, "Error: " + error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfSignedIn() {
        val loginBtn = binding.loginBtn
        val logoutBtn = binding.logoutBtn
        val bookmarksBtn = binding.bookmarksBtn

        val user = firebaseAuth.currentUser
        if (user != null) {
            Toast.makeText(this, "Current user: " + user.email, Toast.LENGTH_SHORT).show()
            loginBtn.visibility = View.GONE
            logoutBtn.visibility = View.VISIBLE
            bookmarksBtn.visibility = View.VISIBLE
        } else {
            Toast.makeText(this, "Login to save bookmarks!", Toast.LENGTH_SHORT).show()
            loginBtn.visibility = View.VISIBLE
            logoutBtn.visibility = View.GONE
            bookmarksBtn.visibility = View.GONE
        }

        storyAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val NUM_STORIES_PER_LOADING_EVENT = 20
    }
}
