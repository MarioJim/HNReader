package org.team4.hnreader.data

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import org.team4.hnreader.data.local.DBHelper
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.remote.ApiRequestQueue

class ItemFinder(ctx: Context) {
    companion object {
        @Volatile
        private var instance: ItemFinder? = null
        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                instance ?: ItemFinder(ctx).also { instance = it }
            }
    }

    private val dbHelper by lazy { DBHelper(ctx) }

    fun getStory(
        id: Int,
        fromCache: Boolean,
        listener: Response.Listener<Story>,
        errorListener: Response.ErrorListener
    ) {
        if (fromCache) {
            val storyFromCache = dbHelper.getStory(id)
            if (storyFromCache == null) {
                getStoryFromApi(id, listener, errorListener)
            } else {
                ApiRequestQueue.getInstance().fetchStory(
                    id,
                    Request.Priority.LOW,
                    { dbHelper.insertOrUpdateStory(it) },
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
        errorListener: Response.ErrorListener
    ) = ApiRequestQueue.getInstance().fetchStory(
        id,
        Request.Priority.HIGH,
        {
            dbHelper.insertOrUpdateStory(it)
            listener.onResponse(it)
        },
        errorListener
    )

}
