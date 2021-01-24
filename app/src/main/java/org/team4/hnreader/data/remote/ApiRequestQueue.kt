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

        private const val ALGOLIA_URL = "https://hn.algolia.com/api/v1/search"
        fun getCommentsURL(storyID: Int, page: Int) =
            "$ALGOLIA_URL?tags=comment,story_$storyID&page=$page"

        const val FIREBASE_URL = "https://hacker-news.firebaseio.com/v0"
        private const val TOP_STORIES_IDS_URL = "$FIREBASE_URL/topstories.json"
        private const val NEW_STORIES_IDS_URL = "$FIREBASE_URL/newstories.json"
        private const val BEST_STORIES_IDS_URL = "$FIREBASE_URL/beststories.json"
        private const val ASK_STORIES_IDS_URL = "$FIREBASE_URL/askstories.json"
        private const val SHOW_STORIES_IDS_URL = "$FIREBASE_URL/showstories.json"
    }

    private val requestQueue: RequestQueue by lazy {
        RequestQueue(NoCache(), BasicNetwork(HurlStack())).apply { start() }
    }

    fun fetchStoryComments(
        storyID: Int,
        page: Int,
        listener: Response.Listener<List<Comment>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoryCommentsRequest(storyID, page, null, listener, errorListener))
    }

    fun fetchTopStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest(TOP_STORIES_IDS_URL, listener, errorListener))
    }

    fun fetchNewStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest(NEW_STORIES_IDS_URL, listener, errorListener))
    }

    fun fetchBestStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest(BEST_STORIES_IDS_URL, listener, errorListener))
    }

    fun fetchAskStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest(ASK_STORIES_IDS_URL, listener, errorListener))
    }

    fun fetchShowStoriesIds(
        listener: Response.Listener<List<Int>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoriesIdsRequest(SHOW_STORIES_IDS_URL, listener, errorListener))
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
