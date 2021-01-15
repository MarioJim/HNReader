package org.team4.hnreader.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import org.team4.hnreader.data.model.COMMENT_TYPE
import org.team4.hnreader.data.model.Comment
import java.lang.RuntimeException

private const val DB_FILE = "cache.sqlite"

private const val TABLE_ITEMS = "items"
private const val FIELD_BY = "user"
private const val FIELD_ID = "id"
private const val FIELD_SCORE = "score"
private const val FIELD_TEXT = "content"
private const val FIELD_TIME = "time"
private const val FIELD_TITLE = "title"
private const val FIELD_TYPE = "type"
private const val FIELD_URL = "url"

private const val TABLE_KIDS = "kids"
private const val FIELD_PARENT_ID = "parent"
private const val FIELD_KID_ID = "kid"

// TODO: Remove queries to insert fake data
private const val FAKE_POPULATE_ITEMS = """
    INSERT INTO $TABLE_ITEMS ($FIELD_BY, $FIELD_ID, $FIELD_TEXT, $FIELD_TIME, $FIELD_TYPE)
    VALUES ("Mario", 123, "Chido tu post", 1610679580, "$COMMENT_TYPE"),
           ("Kevin", 234, "Chale con tu post", 1610679682, "$COMMENT_TYPE"),
           ("José", 345, "Épico tu post", 1610679592, "$COMMENT_TYPE"),
           ("Memo", 456, "No se agüite", 1610679632, "$COMMENT_TYPE")
"""

private const val FAKE_POPULATE_KIDS = """
    INSERT INTO $TABLE_KIDS ($FIELD_PARENT_ID, $FIELD_KID_ID)
    VALUES (1, 123), (1, 234), (1, 345), (1, 456)
"""

class DBHelper(ctx: Context) : SQLiteOpenHelper(ctx, DB_FILE, null, 2) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createItemsTable = "CREATE TABLE $TABLE_ITEMS (" +
                "$FIELD_BY TEXT," +
                "$FIELD_ID INTEGER PRIMARY KEY," +
                "$FIELD_SCORE INTEGER," +
                "$FIELD_TEXT TEXT, " +
                "$FIELD_TIME INTEGER," +
                "$FIELD_TITLE TEXT," +
                "$FIELD_TYPE TEXT," +
                "$FIELD_URL TEXT )"
        val createKidsTable = "CREATE TABLE $TABLE_KIDS (" +
                "$FIELD_PARENT_ID INTEGER," +
                "$FIELD_KID_ID INTEGER PRIMARY KEY )"

        db?.run {
            execSQL(createItemsTable)
            execSQL(createKidsTable)

            // TODO: Remove inserting fake data
            execSQL(FAKE_POPULATE_ITEMS)
            execSQL(FAKE_POPULATE_KIDS)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropItemsTable = "DROP TABLE IF EXISTS $TABLE_ITEMS"
        val dropKidsTable = "DROP TABLE IF EXISTS $TABLE_KIDS"
        db?.run {
            execSQL(dropItemsTable)
            execSQL(dropKidsTable)
        }
        onCreate(db)
    }

    fun addComment(parentID: Int, comment: Comment) {
        val valuesItems = ContentValues().apply {
            put(FIELD_BY, comment.by)
            put(FIELD_ID, comment.id)
            put(FIELD_TEXT, comment.text)
            put(FIELD_TIME, comment.time)
            put(FIELD_TYPE, comment.type)
        }

        val valuesKids = ContentValues().apply {
            put(FIELD_PARENT_ID, parentID)
            put(FIELD_KID_ID, comment.id)
        }

        writableDatabase.transaction {
            writableDatabase.insert(TABLE_ITEMS, null, valuesItems)
            writableDatabase.insert(TABLE_KIDS, null, valuesKids)
        }
    }

    fun getComments(parentID: Int): List<Comment> {
        val cursor = readableDatabase.query(
            TABLE_KIDS,
            null,
            "$FIELD_PARENT_ID = $parentID",
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
                TABLE_ITEMS,
                arrayOf(FIELD_BY, FIELD_TEXT, FIELD_TIME),
                "$FIELD_ID = $kidID AND $FIELD_TYPE = \"$COMMENT_TYPE\"",
                null,
                null,
                null,
                null
            )
            val found = cursor2.moveToFirst()
            if (found) {
                val comment = Comment(
                    cursor2.getString(0),
                    kidID,
                    cursor2.getString(1),
                    cursor2.getInt(2)
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
}
