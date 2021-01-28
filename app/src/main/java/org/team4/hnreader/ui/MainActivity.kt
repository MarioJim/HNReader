package org.team4.hnreader.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.ActivityMainBinding
import org.team4.hnreader.ui.activities.BookmarksActivity
import org.team4.hnreader.ui.activities.LoginActivity
import org.team4.hnreader.ui.callbacks.ShowCommentMenu
import org.team4.hnreader.ui.fragments.CommentOptionsBottomSheet

class MainActivity : AppCompatActivity(), ShowCommentMenu {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        binding.bookmarksBtn.setOnClickListener {
            val intentToBookmarks = Intent(this, BookmarksActivity::class.java)
            startActivity(intentToBookmarks)
        }
        binding.loginBtn.setOnClickListener {
            val intentToLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentToLogin)
        }
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkIfSignedIn()
        }

        checkIfSignedIn()
    }

    private fun checkIfSignedIn() {
        val loginBtn = binding.loginBtn
        val logoutBtn = binding.logoutBtn
        val bookmarksBtn = binding.bookmarksBtn

        val user = firebaseAuth.currentUser
        if (user != null) {
            Toast.makeText(this, "Current user: " + user.email, Toast.LENGTH_SHORT).show()
            loginBtn.visibility = View.GONE
            logoutBtn.visibility = View.VISIBLE
            bookmarksBtn.visibility = View.VISIBLE
        } else {
            Toast.makeText(this, "Login to save bookmarks!", Toast.LENGTH_SHORT).show()
            loginBtn.visibility = View.VISIBLE
            logoutBtn.visibility = View.GONE
            bookmarksBtn.visibility = View.GONE
        }

        // TODO: Find way to notify StoriesRecyclerViewFragment
    }

    override fun showCommentMenu(comment: FlattenedComment) {
        CommentOptionsBottomSheet(comment).show(
            supportFragmentManager,
            CommentOptionsBottomSheet.TAG,
        )
    }
}
