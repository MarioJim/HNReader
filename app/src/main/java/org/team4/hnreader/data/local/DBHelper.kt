package org.team4.hnreader.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import org.team4.hnreader.data.model.*

class DBHelper(ctx: Context) : SQLiteOpenHelper(ctx, DB_FILE, null, 7) {
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

    fun insertOrUpdateItem(item: Item) {
        writableDatabase.transaction {
            writableDatabase.insertWithOnConflict(
                ItemsTable.TABLE_NAME,
                null,
                item.toItemsContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE
            )
            item.toKidsContentValues().forEach { values ->
                writableDatabase.insertWithOnConflict(
                    KidsTable.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
        }
    }

    fun getComment(id: Int): Comment? {
        val cursor = readableDatabase.query(
            ItemsTable.TABLE_NAME,
            arrayOf(ItemsTable.FIELD_AUTHOR, ItemsTable.FIELD_CREATED_AT, ItemsTable.FIELD_TEXT),
            "${ItemsTable.FIELD_ID} = $id",
            null,
            null,
            null,
            null
        )
        val comment = if (cursor.moveToFirst()) {
            Comment(
                cursor.getString(0),
                cursor.getInt(1),
                id,
                getKids(id),
                cursor.getString(2)
            )
        } else { null }
        cursor.close()
        return comment
    }

    fun getStory(id: Int): Story? {
        val cursor = readableDatabase.query(
            ItemsTable.TABLE_NAME,
            arrayOf(
                ItemsTable.FIELD_AUTHOR,
                ItemsTable.FIELD_CREATED_AT,
                ItemsTable.FIELD_NUM_COMMENTS,
                ItemsTable.FIELD_POINTS,
                ItemsTable.FIELD_TEXT,
                ItemsTable.FIELD_TITLE,
                ItemsTable.FIELD_URL
            ),
            "${ItemsTable.FIELD_ID} = $id",
            null,
            null,
            null,
            null
        )
        val story = if (cursor.moveToFirst()) {
            val url = cursor.getString(6)
            if (url == null) {
                StoryWithText(
                    cursor.getString(0),
                    cursor.getInt(1),
                    id,
                    getKids(id),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4) ?: "",
                    cursor.getString(5)
                )
            } else {
                StoryWithURL(
                    cursor.getString(0),
                    cursor.getInt(1),
                    id,
                    getKids(id),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(5),
                    url,
                )
            }
        } else { null }
        cursor.close()
        return story
    }

    private fun getKids(id: Int): List<Int> {
        val cursor = readableDatabase.query(
            KidsTable.TABLE_NAME,
            arrayOf(KidsTable.FIELD_KID_ID),
            "${KidsTable.FIELD_PARENT_ID} = $id",
            null,
            null,
            null,
            null
        )
        val kids = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map { cursor.getInt(0) }
            .toList()
        cursor.close()
        return kids
    }
}
