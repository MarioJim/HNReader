package org.team4.hnreader.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import org.team4.hnreader.databinding.ActivityCommentsBinding
import org.team4.hnreader.ui.CommentAdapter
import org.team4.hnreader.data.local.DBHelper

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Comments"

        dbHelper = DBHelper(this)
        getComments()
    }

    private fun getComments() {
        val commentsList = dbHelper.getComments(1)

        binding.goToAddCommentBtn.setOnClickListener {
            val intentToAddComment = Intent(this, AddCommentActivity::class.java)
            startActivity(intentToAddComment)
        }

        binding.recyclerviewComments.adapter = CommentAdapter(commentsList)
        binding.recyclerviewComments.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewComments.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        getComments()
    }
}
