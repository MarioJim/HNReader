package org.team4.hnreader.data.remote

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONArray
import org.json.JSONException
import java.nio.charset.Charset

class StoriesIdsRequest(
    url: String,
    private val listener: Response.Listener<List<Int>>,
    errorListener: Response.ErrorListener
) : Request<List<Int>>(Method.GET, url, errorListener) {

    override fun getPriority() = Priority.IMMEDIATE

    override fun deliverResponse(response: List<Int>) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<List<Int>> {
        return try {
            val jsonStr = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )
            val jsonArray = JSONArray(jsonStr)
            val storyIdsList = List(jsonArray.length()) { jsonArray.getInt(it) }
            Response.success(storyIdsList, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: JSONException) {
            Response.error(ParseError(e))
        }
    }
}
