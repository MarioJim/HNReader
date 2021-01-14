package org.team4.hnreader.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.team4.hnreader.databinding.ActivityCommentsBinding

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Comments"

        binding.goToAddCommentBtn.setOnClickListener {
            val intentToAddComment = Intent(this, AddCommentActivity::class.java)
            startActivity(intentToAddComment)
        }
    }
}
