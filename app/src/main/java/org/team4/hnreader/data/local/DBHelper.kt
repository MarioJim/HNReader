package org.team4.hnreader.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import org.team4.hnreader.data.model.COMMENT_TYPE
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.STORY_TYPE
import org.team4.hnreader.data.model.Story

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
    INSERT INTO $TABLE_ITEMS ($FIELD_BY, $FIELD_ID, $FIELD_SCORE, $FIELD_TEXT, $FIELD_TIME, $FIELD_TITLE, $FIELD_TYPE, $FIELD_URL)
    VALUES ("Mario", 123, null, "Chido tu post", 1610679580, null, "$COMMENT_TYPE", null),
           ("Kevin", 234, null, "Chale con tu post", 1610679682, null, "$COMMENT_TYPE", null),
           ("Fausto", 345, null, "Nos quedó rifada la app", 1610679592, null, "$COMMENT_TYPE", null),
           ("Memo", 456, null, "Se merecen un 100, no se agüiten", 1610679632, null, "$COMMENT_TYPE", null),
           ("Mario", 111, 9000, null, 1610678632, "Arch Linux", "$STORY_TYPE", "https://wiki.archlinux.org/"),
           ("Kevin", 222, 420, null, 1610678632, "Mac OS", "$STORY_TYPE", "https://www.apple.com/macos/big-sur/"),
           ("Fausto", 333, 69, null, 1610678632, "Windows", "$STORY_TYPE", "https://www.microsoft.com/en-us/windows"),
           ("Memo", 444, 1337, null, 1610678632, "Pop! OS", "$STORY_TYPE", "https://pop.system76.com/")
"""

private const val FAKE_POPULATE_KIDS = """
    INSERT INTO $TABLE_KIDS ($FIELD_PARENT_ID, $FIELD_KID_ID)
    VALUES (111, 123), (111, 234), (111, 345), (111, 456),
           (222, 123), (222, 234), (222, 345), (222, 456),
           (333, 123), (333, 234), (333, 345), (333, 456),
           (444, 123), (444, 234), (444, 345), (444, 456)
"""

class DBHelper(ctx: Context) : SQLiteOpenHelper(ctx, DB_FILE, null, 5) {
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
                "$FIELD_KID_ID INTEGER )"

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

    fun getStories(): List<Story> {
        val cursor = readableDatabase.query(
            TABLE_ITEMS,
            arrayOf(FIELD_BY, FIELD_ID, FIELD_SCORE, FIELD_TIME, FIELD_TITLE, FIELD_URL),
            "$FIELD_TYPE = \"$STORY_TYPE\"",
            null,
            null,
            null,
            null
        )
        val result = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map {
                val id = cursor.getInt(1)
                val cursor2 = readableDatabase.query(
                    TABLE_KIDS,
                    arrayOf(FIELD_PARENT_ID, "COUNT(*) AS C"),
                    "$FIELD_PARENT_ID = $id",
                    null,
                    FIELD_PARENT_ID,
                    null,
                    null
                )
                cursor2.moveToFirst()
                val numComments = cursor2.getInt(1)
                cursor2.close()

                return@map Story(
                    cursor.getString(0),
                    id,
                    numComments,
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5)
                )
            }
            .toList()
        cursor.close()
        return result
    }
}
