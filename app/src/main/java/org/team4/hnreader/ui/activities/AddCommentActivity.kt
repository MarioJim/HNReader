package org.team4.hnreader.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.team4.hnreader.databinding.ActivityAddCommentBinding
import org.team4.hnreader.data.local.DBHelper
import org.team4.hnreader.data.model.Comment
import kotlin.random.Random

class AddCommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCommentBinding

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Add a comment"

        binding.cancelAddCommentBtn.setOnClickListener {
            finish()
        }

        binding.addCommentBtn.setOnClickListener {
            addComment()
        }

        dbHelper = DBHelper(this)
    }

    private fun addComment() {
        val newComment = binding.teComment.text.toString()

        if (newComment.isNotEmpty()) {
            dbHelper.addComment(
                1, Comment(
                    "UwU",
                    Random.nextInt(),
                    newComment,
                    Random.nextInt()
                )
            )
            finish()
        } else {
            Toast.makeText(this, "Comments can not be empty", Toast.LENGTH_SHORT).show()
        }
    }
}