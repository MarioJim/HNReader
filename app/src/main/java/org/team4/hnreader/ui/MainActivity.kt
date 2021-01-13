package org.team4.hnreader.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.team4.hnreader.databinding.ActivityMainBinding
import org.team4.hnreader.ui.activities.BookmarksActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bookmarksBtn.setOnClickListener {
            val intentToNavDrawer = Intent(this, BookmarksActivity::class.java)
            startActivity(intentToNavDrawer)
        }
    }
}
