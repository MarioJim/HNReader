package org.team4.hnreader.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.team4.hnreader.data.local.DBHelper
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.databinding.ActivityAddCommentBinding
import kotlin.random.Random

class AddCommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCommentBinding
    private lateinit var dbHelper: DBHelper
    private var parentID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Add a comment"

        parentID = intent.getIntExtra("parent_id", -1)
        if (parentID == -1) {
            finish()
        }

        binding.cancelAddCommentBtn.setOnClickListener {
            finish()
        }
        binding.addCommentBtn.setOnClickListener {
            val newComment = binding.teComment.text.toString()

            if (newComment.isNotEmpty()) {
                val comment = Comment(
                    "Kevin",
                    Random.nextInt(),
                    newComment,
                    Random.nextInt()
                )
                dbHelper.addComment(parentID, comment)
                finish()
            } else {
                Toast.makeText(this, "Comments can not be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dbHelper = DBHelper(this)
    }
}
