package org.team4.hnreader.data.remote

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.NoCache
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.Story

class HackerNewsApi {
    companion object {
        @Volatile
        private var instance: HackerNewsApi? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: HackerNewsApi().also { instance = it }
            }

        private const val ALGOLIA_URL = "https://hn.algolia.com/api/v1/search"
        fun getFrontPageURL(page: Int) = "$ALGOLIA_URL?tags=front_page,story&page=$page"
        fun getCommentsURL(storyID: Int, page: Int) =
            "$ALGOLIA_URL?tags=comment,story_$storyID&page=$page"
    }

    private val requestQueue: RequestQueue by lazy {
        RequestQueue(NoCache(), BasicNetwork(HurlStack())).apply { start() }
    }

    fun fetchFrontPage(
        page: Int,
        listener: Response.Listener<List<Story>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(FrontPageRequest(page, null, listener, errorListener))
    }

    fun fetchStoryComments(
        storyID: Int,
        page: Int,
        listener: Response.Listener<List<Comment>>,
        errorListener: Response.ErrorListener
    ) {
        requestQueue.add(StoryCommentsRequest(storyID, page, null, listener, errorListener))
    }
}
