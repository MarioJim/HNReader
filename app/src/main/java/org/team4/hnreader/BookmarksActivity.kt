package org.team4.hnreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.team4.hnreader.databinding.ActivityBookmarksBinding

class BookmarksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Saved bookmarks"
    }
}