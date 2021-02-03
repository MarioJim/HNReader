package org.team4.hnreader.ui

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import org.team4.hnreader.R
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.databinding.ActivityMainBinding
import org.team4.hnreader.ui.callbacks.ShowCommentMenu
import org.team4.hnreader.ui.fragments.CommentOptionsBottomSheet

class MainActivity : AppCompatActivity(), ShowCommentMenu {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainContent) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.bookmarksStoriesFragment,
                R.id.bookmarksCommentsFragment,
                R.id.topStoriesFragment,
                R.id.newStoriesFragment,
                R.id.bestStoriesFragment,
                R.id.askHNFragment,
                R.id.showHNFragment,
                R.id.settingsFragment,
            ),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navDrawer.setupWithNavController(navController)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener {
            val intentToLogin = Intent(this, LoginActivity::class.java)
            startActivityForResult(intentToLogin, LOGIN_ACTIVITY_CODE)
        }
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            recreate()
        }

        firebaseAuth.addAuthStateListener {
            binding.navDrawer.menu.setGroupVisible(R.id.bookmarksGroup, it.currentUser != null)
            val tvUserDrawer =
                binding.navDrawer.getHeaderView(0).findViewById<TextView>(R.id.tvUserDrawer)
            if (it.currentUser == null) {
                tvUserDrawer.visibility = View.GONE
            } else {
                tvUserDrawer.visibility = View.VISIBLE
                tvUserDrawer.text = it.currentUser?.email ?: "Logged in"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_ACTIVITY_CODE) {
            recreate()
        }
    }

    override fun onStart() {
        super.onStart()

        checkIfSignedIn()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainContent) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkIfSignedIn() {
        val loginBtn = binding.loginBtn
        val logoutBtn = binding.logoutBtn

        val user = firebaseAuth.currentUser
        if (user != null) {
            loginBtn.visibility = View.GONE
        } else {
            logoutBtn.visibility = View.GONE
        }
    }

    override fun showCommentMenu(comment: FlattenedComment) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        CommentOptionsBottomSheet(comment, clipboard)
            .show(supportFragmentManager, CommentOptionsBottomSheet.TAG)
    }

    companion object {
        private const val LOGIN_ACTIVITY_CODE = 0
    }
}
