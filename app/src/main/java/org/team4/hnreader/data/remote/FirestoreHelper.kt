package org.team4.hnreader.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import java.util.*

class FirestoreHelper() {
    companion object {
        @Volatile
        private var instance: FirestoreHelper? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FirestoreHelper().also { instance = it }
            }
    }

    private var db = Firebase.firestore
    private var firebaseAuth = FirebaseAuth.getInstance()

    fun addStoryToBookmarks(story: Story, callback: (Pair<Boolean, String>) -> Unit) {
        val storyMap = mapOf("save-date" to Calendar.getInstance().time, "id" to story.id)

        db.collection("users/${firebaseAuth.currentUser?.uid}/bookmarks-stories")
            .document(story.id.toString()).set(storyMap)
            .addOnCompleteListener { task ->
                callback(
                    Pair(task.isSuccessful,
                        if (task.isSuccessful) "Success saving story to bookmarks" else task.exception?.message ?: "Error")
                )
            }
    }

    fun getStoriesFromBookmarks(callback: (List<Int>) -> Unit) {
        db.collection("users/${firebaseAuth.currentUser?.uid}/bookmarks-stories")
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

    fun addCommentToBookmarks(comment: FlattenedComment, callback: (Pair<Boolean, String>) -> Unit) {
        val commentMap = mapOf("save-date" to Calendar.getInstance().time, "id" to comment.id)
        db.collection("users/${firebaseAuth.currentUser?.uid}/bookmarks-comments")
            .document(comment.id.toString()).set(commentMap)
            .addOnCompleteListener { task ->
                callback(
                    Pair(task.isSuccessful,
                        if (task.isSuccessful) "Success saving comment to bookmarks" else task.exception?.message ?: "Error")
                )
            }
    }

    fun getCommentsFromBookmarks(callback: (List<Int>) -> Unit) {
        db.collection("users/${firebaseAuth.currentUser?.uid}/bookmarks-comments")
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
}