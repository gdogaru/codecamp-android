package com.gdogaru.codecamp.db


import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "bookmarks", primaryKeys = ["eventId", "sessionId"])
data class Bookmark(

        @ColumnInfo(name = "eventId")
        val eventId: String,

        @ColumnInfo(name = "sessionId")
        val sessionId: String

)