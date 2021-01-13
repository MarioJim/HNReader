package org.team4.hnreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class BookmarksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)

        title = "Saved bookmarks"
    }
}