package org.team4.hnreader.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import java.util.*

class FirestoreHelper {
    companion object {
        @Volatile
        private var instance: FirestoreHelper? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FirestoreHelper().also { instance = it }
            }

        fun getBookmarkedStoriesPath(userId: String) = "users/$userId/bookmarks-stories"
        fun getBookmarkedCommentsPath(userId: String) = "users/$userId/bookmarks-comments"
    }

    private var db = Firebase.firestore
    private var firebaseAuth = FirebaseAuth.getInstance()

    fun addStoryToBookmarks(story: Story, callback: (Pair<Boolean, String>) -> Unit) {
        val storyMap = mapOf("save-date" to Calendar.getInstance().time, "id" to story.id)
        db.collection(getBookmarkedStoriesPath(firebaseAuth.currentUser?.uid!!))
            .document(story.id.toString()).set(storyMap)
            .addOnCompleteListener { task ->
                val message = if (task.isSuccessful) {
                    "Success saving story to bookmarks"
                } else {
                    task.exception?.message ?: "Error"
                }
                callback(Pair(task.isSuccessful, message))
            }
    }

    fun removeStoryFromBookmarks(story: Story, callback: (Pair<Boolean, String>) -> Unit) {
        db.collection(getBookmarkedStoriesPath(firebaseAuth.currentUser?.uid!!))
            .document(story.id.toString()).delete()
            .addOnCompleteListener { task ->
                val message = if (task.isSuccessful) {
                    "Success removing story from bookmarks"
                } else {
                    task.exception?.message ?: "Error"
                }
                callback(Pair(task.isSuccessful, message))
            }
    }

    fun getStoriesFromBookmarks(callback: (List<Int>) -> Unit) {
        db.collection(getBookmarkedStoriesPath(firebaseAuth.currentUser?.uid!!))
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = ArrayList<Int>()
                    for (document in task.result!!) {
                        val story = document.id.toInt()
                        list.add(story)
                    }
                    callback(list)
                }
            }
    }

    fun checkIfStoryIsBookmark(story: Story, callback: (Boolean) -> Unit) {
        db.collection(getBookmarkedStoriesPath(firebaseAuth.currentUser?.uid!!))
            .document(story.id.toString()).get()
            .addOnCompleteListener { task ->
                callback(task.result?.exists() ?: false)
            }
    }

    fun addCommentToBookmarks(
        comment: FlattenedComment,
        callback: (Pair<Boolean, String>) -> Unit,
    ) {
        val commentMap = mapOf("save-date" to Calendar.getInstance().time, "id" to comment.id)
        db.collection(getBookmarkedCommentsPath(firebaseAuth.currentUser?.uid!!))
            .document(comment.id.toString()).set(commentMap)
            .addOnCompleteListener { task ->
                val message = if (task.isSuccessful) {
                    "Success saving comment from bookmarks"
                } else {
                    task.exception?.message ?: "Error"
                }
                callback(Pair(task.isSuccessful, message))
            }
    }

    fun removeCommentFromBookmarks(
        comment: FlattenedComment,
        callback: (Pair<Boolean, String>) -> Unit,
    ) {
        db.collection(getBookmarkedCommentsPath(firebaseAuth.currentUser?.uid!!))
            .document(comment.id.toString()).delete()
            .addOnCompleteListener { task ->
                val message = if (task.isSuccessful) {
                    "Success removing comment to bookmarks"
                } else {
                    task.exception?.message ?: "Error"
                }
                callback(Pair(task.isSuccessful, message))
            }
    }

    fun getCommentsFromBookmarks(callback: (List<Int>) -> Unit) {
        db.collection(getBookmarkedCommentsPath(firebaseAuth.currentUser?.uid!!))
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = ArrayList<Int>()
                    for (document in task.result!!) {
                        val story = document.id.toInt()
                        list.add(story)
                    }
                    callback(list)
                }
            }
    }

    fun checkIfCommentIsBookmark(comment: FlattenedComment, callback: (Boolean) -> Unit) {
        db.collection(getBookmarkedCommentsPath(firebaseAuth.currentUser?.uid!!))
            .document(comment.id.toString()).get()
            .addOnCompleteListener { task ->
                callback(task.result?.exists() ?: false)
            }
    }
}
