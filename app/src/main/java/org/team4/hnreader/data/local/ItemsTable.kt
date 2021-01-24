package org.team4.hnreader.data.local

import android.database.Cursor
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.StoryWithText
import org.team4.hnreader.data.model.StoryWithURL

class ItemsTable {
    companion object {
        const val TABLE_NAME = "items"
        const val FIELD_AUTHOR = "author"
        const val FIELD_CREATED_AT = "created_at"
        const val FIELD_ID = "id"
        const val FIELD_NUM_COMMENTS = "num_comments"
        const val FIELD_POINTS = "points"
        const val FIELD_TEXT = "content"
        const val FIELD_TITLE = "title"
        const val FIELD_TYPE = "type"
        const val FIELD_URL = "url"

        val createTableStatement: String
            get() = "CREATE TABLE $TABLE_NAME (" +
                    "$FIELD_AUTHOR TEXT," +
                    "$FIELD_CREATED_AT INTEGER," +
                    "$FIELD_ID INTEGER PRIMARY KEY," +
                    "$FIELD_NUM_COMMENTS INTEGER," +
                    "$FIELD_POINTS INTEGER," +
                    "$FIELD_TEXT TEXT," +
                    "$FIELD_TITLE TEXT," +
                    "$FIELD_TYPE TEXT," +
                    "$FIELD_URL TEXT)"

        val dropTableStatement: String
            get() = "DROP TABLE IF EXISTS $TABLE_NAME"

        val fieldsForStory = arrayOf(
            FIELD_AUTHOR,
            FIELD_CREATED_AT,
            FIELD_ID,
            FIELD_NUM_COMMENTS,
            FIELD_POINTS,
            FIELD_TEXT,
            FIELD_TITLE,
            FIELD_URL
        )

        fun parseStory(cursor: Cursor): Story {
            val url = cursor.getString(7)
            return if (url == null) {
                StoryWithText(
                    cursor.getString(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getString(5) ?: "",
                    cursor.getString(6)
                )
            } else {
                StoryWithURL(
                    cursor.getString(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getString(6),
                    url,
                )
            }
        }
    }
}
