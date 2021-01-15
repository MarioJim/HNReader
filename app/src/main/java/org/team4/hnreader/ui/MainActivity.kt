package org.team4.hnreader.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.team4.hnreader.data.local.DBHelper
import org.team4.hnreader.databinding.ActivityMainBinding
import org.team4.hnreader.ui.activities.BookmarksActivity
import org.team4.hnreader.ui.adapters.StoryAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        binding.bookmarksBtn.setOnClickListener {
            val intentToBookmarks = Intent(this, BookmarksActivity::class.java)
            startActivity(intentToBookmarks)
        }
        binding.recyclerviewStories.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewStories.setHasFixedSize(true)

        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        val storiesList = dbHelper.getStories()
        binding.recyclerviewStories.adapter = StoryAdapter(storiesList)
    }
}
