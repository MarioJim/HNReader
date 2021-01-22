package org.team4.hnreader.data.remote

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONException
import org.json.JSONObject
import org.team4.hnreader.data.model.Comment
import java.nio.charset.Charset

class StoryCommentsRequest(
    storyID: Int,
    page: Int,
    private val headers: MutableMap<String, String>?,
    private val listener: Response.Listener<List<Comment>>,
    errorListener: Response.ErrorListener
) : Request<List<Comment>>(Method.GET, HackerNewsApi.getCommentsURL(storyID, page), errorListener) {

    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun deliverResponse(response: List<Comment>) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<List<Comment>> {
        return try {
            val jsonStr = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )
            val jsonArray = JSONObject(jsonStr).getJSONArray("hits")
            val commentsList = List(jsonArray.length()) { i ->
                Comment.fromJSONObject(jsonArray.getJSONObject(i))
            }
            Response.success(commentsList, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: JSONException) {
            Response.error(ParseError(e))
        }
    }
}
