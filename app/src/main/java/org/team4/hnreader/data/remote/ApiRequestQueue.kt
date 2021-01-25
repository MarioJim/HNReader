package org.team4.hnreader.data.remote

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.NoCache
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.Story

class ApiRequestQueue {
    companion object {
        @Volatile
        private var instance: ApiRequestQueue? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: ApiRequestQueue().also { instance = it }
            }

        const val BASE_URL = "https://hacker-news.firebaseio.com/v0"
    }

    private val requestQueue: RequestQueue by lazy {
        RequestQueue(NoCache(), BasicNetwork(HurlStack())).apply { start() }
    }

    fun fetchTopStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest("$BASE_URL/topstories.json", listener, errorListener))
    }

    fun fetchNewStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest("$BASE_URL/newstories.json", listener, errorListener))
    }

    fun fetchBestStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest("$BASE_URL/beststories.json", listener, errorListener))
    }

    fun fetchAskStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest("$BASE_URL/askstories.json", listener, errorListener))
    }

    fun fetchShowStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest("$BASE_URL/showstories.json", listener, errorListener))
    }

    fun fetchStory(
        id: Int,
        priority: Request.Priority,
        listener: Response.Listener<Story>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(ItemRequest(
            id,
            priority,
            { listener.onResponse(it as Story) },
            errorListener
        ))
    }

    fun fetchComment(
        id: Int,
        listener: Response.Listener<Comment>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(ItemRequest(
            id,
            Request.Priority.NORMAL,
            { listener.onResponse(it as Comment) },
            errorListener
        ))
    }
}
