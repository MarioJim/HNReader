package org.team4.hnreader.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.team4.hnreader.R
import org.team4.hnreader.data.local.DBHelper
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.ActivityCommentsBinding
import org.team4.hnreader.ui.adapters.CommentAdapter
import org.team4.hnreader.ui.fragments.StoryFragment

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var story: Story
    private lateinit var storyFragment: StoryFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Comments"

        val nullableStory = intent.getSerializableExtra("story") ?: {
            finish()
        }
        story = nullableStory as Story

        dbHelper = DBHelper(this)

        binding.goToAddCommentBtn.setOnClickListener {
            val intentToAddComment = Intent(this, AddCommentActivity::class.java).apply {
                putExtra("parent_id", story.id)
            }
            startActivity(intentToAddComment)
        }

        binding.recyclerviewComments.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewComments.setHasFixedSize(true)

        storyFragment = StoryFragment.newInstance(story)
        supportFragmentManager.beginTransaction().add(R.id.container, storyFragment).commit()
    }

    override fun onResume() {
        super.onResume()
        val comments = dbHelper.getComments(story.id)
        binding.recyclerviewComments.adapter = CommentAdapter(comments)
        storyFragment.refreshInfo(comments.size)
    }
}
