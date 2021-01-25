package org.team4.hnreader.data.remote

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONException
import org.json.JSONObject
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.Item
import org.team4.hnreader.data.model.Story
import java.nio.charset.Charset

class ItemRequest(
    id: Int,
    private val customPriority: Priority,
    private val listener: Response.Listener<Item>,
    errorListener: Response.ErrorListener
) : Request<Item>(Method.GET, getItemURL(id), errorListener) {

    override fun getPriority() = customPriority

    override fun deliverResponse(response: Item) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<Item> {
        return try {
            val jsonStr = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )
            val jsonObject = JSONObject(jsonStr)
            if (jsonObject.has("deleted") && jsonObject.getBoolean("deleted")) {
                throw DeletedItemException(jsonObject)
            }
            val item = when (jsonObject.getString("type")) {
                "story" -> Story.fromJSONObject(jsonObject)
                "comment" -> Comment.fromJSONObject(jsonObject)
                else -> {
                    val type = jsonObject.getString("type")
                    val id = jsonObject.getInt("id")
                    throw ItemTypeNotImplementedException(type, id)
                }
            }
            Response.success(item, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: JSONException) {
            Response.error(ParseError(e))
        } catch (e: DeletedItemException) {
            Response.error(ParseError(e))
        } catch (e: ItemTypeNotImplementedException) {
            Response.error(ParseError(e))
        }
    }

    companion object {
        fun getItemURL(id: Int) = "${ApiRequestQueue.BASE_URL}/item/$id.json"
    }
}
