package org.team4.hnreader.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.Comment.Companion.COMMENT_TYPE
import org.team4.hnreader.data.model.Story
import org.team4.hnreader.data.model.Story.Companion.STORY_TYPE
import org.team4.hnreader.data.model.StoryWithText
import org.team4.hnreader.data.model.StoryWithURL

class DBHelper(ctx: Context) : SQLiteOpenHelper(ctx, DB_FILE, null, 6) {
    companion object {
        private const val DB_FILE = "cache.sqlite"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.run {
            execSQL(ItemsTable.createTableStatement)
            execSQL(KidsTable.createTableStatement)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.run {
            execSQL(ItemsTable.dropTableStatement)
            execSQL(KidsTable.dropTableStatement)
        }
        onCreate(db)
    }

    fun addComment(comment: Comment) {
        writableDatabase.transaction {
            writableDatabase.insert(
                ItemsTable.TABLE_NAME,
                null,
                comment.toItemsContentValues())
            writableDatabase.insert(
                KidsTable.TABLE_NAME,
                null,
                comment.toKidsContentValues())
        }
    }

    fun getComments(parentID: Int): List<Comment> {
        val cursor = readableDatabase.query(
            KidsTable.TABLE_NAME,
            null,
            "${KidsTable.FIELD_PARENT_ID} = $parentID",
            null,
            null,
            null,
            null
        )
        val kids = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map { cursor.getInt(1) }
            .toList()
        cursor.close()

        val result = ArrayList<Comment>()
        kids.forEach { kidID ->
            val cursor2 = readableDatabase.query(
                ItemsTable.TABLE_NAME,
                arrayOf(ItemsTable.FIELD_AUTHOR, ItemsTable.FIELD_TEXT, ItemsTable.FIELD_CREATED_AT),
                "${ItemsTable.FIELD_ID} = $kidID AND ${ItemsTable.FIELD_TYPE} = \"$COMMENT_TYPE\"",
                null,
                null,
                null,
                null
            )
            val found = cursor2.moveToFirst()
            if (found) {
                val comment = Comment(
                    cursor2.getString(0),
                    cursor2.getInt(2),
                    kidID,
                    parentID,
                    cursor2.getString(1)
                )
                result.add(comment)
            } else {
                // TODO: Implement fetch
                throw RuntimeException("Comment with ID $kidID not found")
            }
            cursor2.close()
        }
        return result
    }

    fun getStories(): List<Story> {
        val cursor = readableDatabase.query(
            ItemsTable.TABLE_NAME,
            arrayOf(
                ItemsTable.FIELD_AUTHOR,
                ItemsTable.FIELD_CREATED_AT,
                ItemsTable.FIELD_ID,
                ItemsTable.FIELD_NUM_COMMENTS,
                ItemsTable.FIELD_POINTS,
                ItemsTable.FIELD_TEXT,
                ItemsTable.FIELD_TITLE,
                ItemsTable.FIELD_URL
            ),
            "${ItemsTable.FIELD_TYPE} = \"$STORY_TYPE\"",
            null,
            null,
            null,
            null
        )
        val result = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map {
                val url = cursor.getString(7)
                if (url == null) {
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
            .toList()
        cursor.close()
        return result
    }
}
