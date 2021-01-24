package org.team4.hnreader.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.team4.hnreader.data.local.DBHelper
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.HackerNewsApi
import org.team4.hnreader.databinding.ActivityMainBinding
import org.team4.hnreader.ui.activities.BookmarksActivity
import org.team4.hnreader.ui.adapters.StoryAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var storyAdapter: StoryAdapter
    private var storiesList: ArrayList<Story> = ArrayList()
    private var pagesLoaded: Int = 0
    private var isLoading: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        dbHelper = DBHelper(this)

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
                if (!isLoading && lastViewedItem + 5 >= storiesList.size) {
                    loadMoreStories()
                    isLoading = true
                }
            }
        })

        // Load first page
        HackerNewsApi.getInstance().fetchFrontPage(0,
            {
                storiesList.plusAssign(it)
                storyAdapter.notifyDataSetChanged()
                isLoading = false
            },
            { Toast.makeText(this, "Error: " + it.message, Toast.LENGTH_LONG).show() })
    }

    private fun loadMoreStories() {
        pagesLoaded++
        Log.e("loading", "Cargando página $pagesLoaded")
        HackerNewsApi.getInstance().fetchFrontPage(pagesLoaded,
            {
                storiesList.addAll(it)
                storyAdapter.notifyDataSetChanged()
                isLoading = false
                Log.e("loading", "Cargó página $pagesLoaded, ${storiesList.size} stories cargadas")
            },
            { Toast.makeText(this, "Error: " + it.message, Toast.LENGTH_LONG).show() })
    }
}
