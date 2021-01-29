package org.team4.hnreader.data

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import org.team4.hnreader.data.local.DBHelper
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.FlattenedComment
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.ApiRequestQueue
import java.util.concurrent.atomic.AtomicInteger

class ItemFinder(ctx: Context?) {
    companion object {
        @Volatile
        private var instance: ItemFinder? = null
        fun getInstance(ctx: Context?) =
            instance ?: synchronized(this) {
                instance ?: ItemFinder(ctx).also { instance = it }
            }
    }

    private val dbHelper by lazy { DBHelper(ctx) }

    fun getStoriesFromIdsList(
        idList: List<Int>,
        fromCache: Boolean,
        listener: Response.Listener<List<Story>>,
        errorListener: Response.ErrorListener,
    ) {
        val storiesToAdd = arrayOfNulls<Story>(idList.size)
        val storiesToAddLeft = AtomicInteger(idList.size)
        for (storyIdx in idList.indices) {
            getStory(
                idList[storyIdx],
                fromCache,
                { story ->
                    storiesToAdd[storyIdx] = story
                    if (storiesToAddLeft.decrementAndGet() == 0)
                        listener.onResponse(storiesToAdd.filterNotNull())
                },
                { error ->
                    errorListener.onErrorResponse(error)
                    if (storiesToAddLeft.decrementAndGet() == 0)
                        listener.onResponse(storiesToAdd.filterNotNull())
                }
            )
        }
    }

    fun getStory(
        id: Int,
        fromCache: Boolean,
        listener: Response.Listener<Story>,
        errorListener: Response.ErrorListener,
    ) {
        if (fromCache) {
            val storyFromCache = dbHelper.getStory(id)
            if (storyFromCache == null) {
                getStoryFromApi(id, listener, errorListener)
            } else {
                ApiRequestQueue.getInstance().fetchItem<Story>(
                    id,
                    Request.Priority.LOW,
                    { dbHelper.insertOrUpdateItem(it) },
                    { Log.w("cache refresh", "Story with id $id couldn't be refreshed") }
                )
                listener.onResponse(storyFromCache)
            }
        } else {
            getStoryFromApi(id, listener, errorListener)
        }
    }

    private fun getStoryFromApi(
        id: Int,
        listener: Response.Listener<Story>,
        errorListener: Response.ErrorListener,
    ) = ApiRequestQueue.getInstance().fetchItem<Story>(
        id,
        Request.Priority.HIGH,
        {
            dbHelper.insertOrUpdateItem(it)
            listener.onResponse(it)
        },
        errorListener
    )

    fun getCommentTreesFromIdsList(
        idList: List<Int>,
        depth: Int,
        fromCache: Boolean,
        listener: Response.Listener<List<FlattenedComment>>,
        errorListener: Response.ErrorListener,
    ) {
        val flattenedComments = arrayOfNulls<List<FlattenedComment>>(idList.size)
        val commentTreesLeft = AtomicInteger(idList.size)
        val finishedFetching = {
            listener.onResponse(
                flattenedComments.filterNotNull().flatMap { it.asSequence() }
            )
        }
        for (kidIdx in idList.indices) {
            // Fetch every kid comment tree as a list of flattened comments
            getCommentTree(
                idList[kidIdx],
                depth,
                fromCache,
                { childFlattenedCommentTree ->
                    flattenedComments[kidIdx] = childFlattenedCommentTree
                    if (commentTreesLeft.decrementAndGet() == 0) finishedFetching()
                },
                { error ->
                    errorListener.onErrorResponse(error)
                    if (commentTreesLeft.decrementAndGet() == 0) finishedFetching()
                }
            )
        }
    }

    private fun getCommentTree(
        parentID: Int,
        depth: Int,
        fromCache: Boolean,
        listener: Response.Listener<List<FlattenedComment>>,
        errorListener: Response.ErrorListener,
    ) {
        // Fetch parent comment
        getComment(
            parentID,
            fromCache,
            fun(parentComment: Comment) {
                // Transform it into a FlattenedComment
                val flatParentComment = FlattenedComment.fromComment(parentComment, depth)
                val result = listOf(flatParentComment)
                if (parentComment.kids.isEmpty()) {
                    // If it has no kids, return it
                    listener.onResponse(result)
                } else {
                    // Else, fetch comments for every id
                    getCommentTreesFromIdsList(
                        parentComment.kids,
                        depth + 1,
                        fromCache,
                        { listener.onResponse(result + it) },
                        errorListener
                    )
                }
            },
            errorListener
        )
    }

    fun getCommentsFromIdsList(
        idList: List<Int>,
        fromCache: Boolean,
        listener: Response.Listener<List<Comment>>,
        errorListener: Response.ErrorListener,
    ) {
        val commentsToAdd = arrayOfNulls<Comment>(idList.size)
        val commentsToAddLeft = AtomicInteger(idList.size)
        for (commentIdx in idList.indices) {
            getComment(
                idList[commentIdx],
                fromCache,
                { comment ->
                    commentsToAdd[commentIdx] = comment
                    if (commentsToAddLeft.decrementAndGet() == 0)
                        listener.onResponse(commentsToAdd.filterNotNull())
                },
                { error ->
                    errorListener.onErrorResponse(error)
                    if (commentsToAddLeft.decrementAndGet() == 0)
                        listener.onResponse(commentsToAdd.filterNotNull())
                }
            )
        }
    }

    private fun getComment(
        id: Int,
        fromCache: Boolean,
        listener: Response.Listener<Comment>,
        errorListener: Response.ErrorListener,
    ) {
        if (fromCache) {
            val commentFromCache = dbHelper.getComment(id)
            if (commentFromCache == null) {
                getCommentFromApi(id, listener, errorListener)
            } else {
                listener.onResponse(commentFromCache)
            }
        } else {
            getCommentFromApi(id, listener, errorListener)
        }
    }

    private fun getCommentFromApi(
        id: Int,
        listener: Response.Listener<Comment>,
        errorListener: Response.ErrorListener,
    ) = ApiRequestQueue.getInstance().fetchItem<Comment>(
        id,
        Request.Priority.NORMAL,
        {
            dbHelper.insertOrUpdateItem(it)
            listener.onResponse(it)
        },
        errorListener
    )
}
