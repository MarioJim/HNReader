package org.team4.hnreader.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.team4.hnreader.R
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.databinding.ActivityCommentsBinding
import org.team4.hnreader.ui.callbacks.ShowCommentMenu
import org.team4.hnreader.ui.fragments.CommentOptionsBottomSheet
import org.team4.hnreader.ui.fragments.CommentsRecyclerViewFragment

class CommentsActivity : AppCompatActivity(), ShowCommentMenu {
    private lateinit var binding: ActivityCommentsBinding
    private lateinit var commentsRecyclerViewFragment: CommentsRecyclerViewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Comments"

        val maybeStory = intent.getSerializableExtra(ARG_STORY) ?: {
            finish()
        }
        val story = maybeStory as Story

        commentsRecyclerViewFragment = CommentsRecyclerViewFragment.newInstance(story)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.commentsContent, commentsRecyclerViewFragment)
            commit()
        }
    }

    override fun showCommentMenu(comment: FlattenedComment) {
        CommentOptionsBottomSheet(comment).show(
            supportFragmentManager,
            CommentOptionsBottomSheet.TAG,
        )
    }

    companion object {
        const val ARG_STORY = "story"
    }
}
