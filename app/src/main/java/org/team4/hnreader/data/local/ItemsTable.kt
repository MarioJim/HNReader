package org.team4.hnreader.data.local

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
    }
}
