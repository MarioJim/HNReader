package org.team4.hnreader.data.remote

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONException
import org.json.JSONObject
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.HNItem
import org.team4.hnreader.data.model.Story
import java.nio.charset.Charset

class ItemRequest(
    id: Int,
    private val customPriority: Priority,
    private val listener: Response.Listener<HNItem>,
    errorListener: Response.ErrorListener
) : Request<HNItem>(Method.GET, getItemURL(id), errorListener) {

    override fun getPriority() = customPriority

    override fun deliverResponse(response: HNItem) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<HNItem> {
        return try {
            val jsonStr = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )
            val jsonObject = JSONObject(jsonStr)
            val item = when (jsonObject.getString("type")) {
                "story" -> Story.fromJSONObject(jsonObject)
                "comment" -> Comment.fromJSONObject(jsonObject)
                else -> {
                    val type = jsonObject.getString("type")
                    val id = jsonObject.getInt("id")
                    throw UnsupportedOperationException("Item type \"$type\" not implemented (id $id)")
                }
            }
            Response.success(item, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: JSONException) {
            Response.error(ParseError(e))
        } catch (e: UnsupportedOperationException) {
            Response.error(ParseError(e))
        }
    }

    companion object {
        fun getItemURL(id: Int) = "${ApiRequestQueue.BASE_URL}/item/$id.json"
    }
}
