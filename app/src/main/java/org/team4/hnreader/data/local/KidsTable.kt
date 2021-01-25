package org.team4.hnreader.data.local

class KidsTable {
    companion object {
        const val TABLE_NAME = "kids"
        const val FIELD_PARENT_ID = "parent"
        const val FIELD_KID_ID = "kid"

        val createTableStatement: String
            get() = "CREATE TABLE $TABLE_NAME (" +
                    "$FIELD_PARENT_ID INTEGER," +
                    "$FIELD_KID_ID INTEGER PRIMARY KEY)"

        val dropTableStatement: String
            get() = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
