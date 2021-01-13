package org.team4.hnreader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.team4.hnreader.databinding.ActivityMainBinding

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