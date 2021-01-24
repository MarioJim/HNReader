package org.team4.hnreader.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import org.team4.hnreader.data.model.Comment
import org.team4.hnreader.data.model.Story

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
                "${ItemsTable.FIELD_ID} = $kidID AND ${ItemsTable.FIELD_TYPE} = \"${Comment.TYPE}\"",
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

    fun getStory(id: Int): Story? {
        val cursor = readableDatabase.query(
            ItemsTable.TABLE_NAME,
            ItemsTable.fieldsForStory,
            "${ItemsTable.FIELD_ID} = $id",
            null,
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val story = ItemsTable.parseStory(cursor)
            cursor.close()
            story
        } else { null }
    }

    fun insertOrUpdateStory(story: Story) {
        writableDatabase.insertWithOnConflict(
            ItemsTable.TABLE_NAME,
            null,
            story.toItemsContentValues(),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }
}
