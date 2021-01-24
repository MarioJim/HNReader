package org.team4.hnreader.data.remote

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONException
import org.json.JSONObject
import org.team4.hnreader.data.model.Story
import java.nio.charset.Charset

class FrontPageRequest(
    val page: Int,
    private val headers: MutableMap<String, String>?,
    private val listener: Response.Listener<List<Story>>,
    errorListener: Response.ErrorListener
) : Request<List<Story>>(Method.GET, HackerNewsApi.getFrontPageURL(page), errorListener) {

    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun deliverResponse(response: List<Story>) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<List<Story>> {
        return try {
            val jsonStr = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )
            val jsonArray = JSONObject(jsonStr).getJSONArray("hits")
            val storyList = List(jsonArray.length()) { i ->
                Story.fromJSONObject(jsonArray.getJSONObject(i))
            }
            Log.e("request", "Página $page, size ${storyList.size}")
            Response.success(storyList, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: JSONException) {
            Response.error(ParseError(e))
        }
    }
}
