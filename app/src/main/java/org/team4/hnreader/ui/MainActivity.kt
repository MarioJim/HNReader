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
import org.team4.hnreader.databinding.ActivityMainBinding
import org.team4.hnreader.ui.activities.BookmarksActivity
import org.team4.hnreader.ui.activities.LoginActivity
import org.team4.hnreader.ui.adapters.StoryAdapter
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private var storiesIds: ArrayList<Int> = ArrayList()
    private var storiesList: ArrayList<Story> = ArrayList()
    private var firebaseAuth: FirebaseAuth? = null

    // Don't load stories until storiesIds is filled
    private var isLoading: Boolean = true

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
                if (!isLoading && lastViewedItem + 7 >= storiesList.size) {
                    loadMoreStories()
                    isLoading = true
                }
            }
        })

        binding.loginBtn.setOnClickListener {
            val intentToLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentToLogin)
        }

        binding.logoutBtn.setOnClickListener {
            firebaseAuth?.signOut()
            checkIfSignedIn()
        }

        // Load story ids
        ApiRequestQueue.getInstance().fetchTopStoriesIds(
            {
                storiesIds.plusAssign(it)
                loadMoreStories()
            },
            { displayError(it) })
    }

    override fun onStart() {
        super.onStart()

        checkIfSignedIn()
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

    private fun checkIfSignedIn() {
        val loginBtn = binding.loginBtn
        val logoutBtn = binding.logoutBtn
        val bookmarksBtn = binding.bookmarksBtn

        val user = firebaseAuth?.currentUser
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
