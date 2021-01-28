package org.team4.hnreader.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.team4.hnreader.R
import org.team4.hnreader.databinding.ActivityBookmarksBinding
import org.team4.hnreader.ui.fragments.BookmarksCommentsFragment
import org.team4.hnreader.ui.fragments.BookmarksStoriesFragment

class BookmarksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarksBinding
    private lateinit var bookmarksStoriesFragment: BookmarksStoriesFragment
    private lateinit var bookmarksCommentsFragment: BookmarksCommentsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        bookmarksStoriesFragment = BookmarksStoriesFragment()
        bookmarksCommentsFragment = BookmarksCommentsFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.bookmarksContainer, bookmarksStoriesFragment)
            commit()
        }

        binding.btnStories.setOnClickListener { view ->
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.bookmarksContainer, bookmarksStoriesFragment)
                commit()
            }
        }

        binding.btnComments.setOnClickListener { view ->
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.bookmarksContainer, bookmarksCommentsFragment)
                commit()
            }
        }
    }
}
