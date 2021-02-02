package org.team4.hnreader.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import java.lang.Exception
import java.util.*

class FirestoreHelper {
    companion object {
        @Volatile
        private var instance: FirestoreHelper? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FirestoreHelper().also { instance = it }
            }

        fun getBookmarkedStoriesPath(userId: String?) = "users/$userId/bookmarks-stories"
        fun getBookmarkedCommentsPath(userId: String?) = "users/$userId/bookmarks-comments"
    }

    private var db = Firebase.firestore
    private var firebaseAuth = FirebaseAuth.getInstance()

    fun addStoryToBookmarks(story: Story,
                            responseCallback: () -> Unit,
                            errorCallback: (Exception) -> Unit) {
        val storyMap = mapOf("save-date" to Calendar.getInstance().time, "id" to story.id)
        db.collection(getBookmarkedStoriesPath(firebaseAuth.currentUser?.uid))
            .document(story.id.toString()).set(storyMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    responseCallback()
                } else if (task.exception != null)  {
                    errorCallback(task.exception!!)
                }
            }
    }

    fun removeStoryFromBookmarks(story: Story,
                                 responseCallback: () -> Unit,
                                 errorCallback: (Exception) -> Unit) {
        db.collection(getBookmarkedStoriesPath(firebaseAuth.currentUser?.uid))
            .document(story.id.toString()).delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    responseCallback()
                } else if (task.exception != null)  {
                    errorCallback(task.exception!!)
                }
            }
    }

    fun getStoriesFromBookmarks(responseCallback: (List<Int>) -> Unit,
                                errorCallback: (Exception) -> Unit) {
        db.collection(getBookmarkedStoriesPath(firebaseAuth.currentUser?.uid))
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = ArrayList<Int>()
                    for (document in task.result!!) {
                        val story = document.id.toInt()
                        list.add(story)
                    }
                    responseCallback(list)
                } else if (task.exception != null) {
                    errorCallback(task.exception!!)
                }
            }
    }

    fun checkIfStoryIsBookmark(story: Story,
                               responseCallback: (Boolean) -> Unit,
                               errorCallback: (Exception) -> Unit) {
        db.collection(getBookmarkedStoriesPath(firebaseAuth.currentUser?.uid))
            .document(story.id.toString()).get()
            .addOnSuccessListener { docSnap ->
                responseCallback(docSnap.exists())
            }
            .addOnFailureListener {
                errorCallback(it)
            }
    }

    fun addCommentToBookmarks(
        comment: FlattenedComment,
        responseCallback: () -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val commentMap = mapOf("save-date" to Calendar.getInstance().time, "id" to comment.id)
        db.collection(getBookmarkedCommentsPath(firebaseAuth.currentUser?.uid))
            .document(comment.id.toString()).set(commentMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    responseCallback()
                } else if (task.exception != null)  {
                    errorCallback(task.exception!!)
                }
            }
    }

    fun removeCommentFromBookmarks(
        comment: FlattenedComment,
        responseCallback: () -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        db.collection(getBookmarkedCommentsPath(firebaseAuth.currentUser?.uid))
            .document(comment.id.toString()).delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    responseCallback()
                } else if (task.exception != null)  {
                    errorCallback(task.exception!!)
                }
            }
    }

    fun getCommentsFromBookmarks(responseCallback: (List<Int>) -> Unit,
                                 errorCallback: (Exception) -> Unit) {
        db.collection(getBookmarkedCommentsPath(firebaseAuth.currentUser?.uid))
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = ArrayList<Int>()
                    for (document in task.result!!) {
                        val story = document.id.toInt()
                        list.add(story)
                    }
                    responseCallback(list)
                } else if (task.exception != null) {
                    errorCallback(task.exception!!)
                }
            }
    }

    fun checkIfCommentIsBookmark(comment: FlattenedComment,
                                 responseCallback: (Boolean) -> Unit,
                                 errorCallback: (Exception) -> Unit) {
        db.collection(getBookmarkedCommentsPath(firebaseAuth.currentUser?.uid))
            .document(comment.id.toString()).get()
            .addOnSuccessListener { docSnap ->
                responseCallback(docSnap.exists())
            }
            .addOnFailureListener {
                errorCallback(it)
            }
    }
}
