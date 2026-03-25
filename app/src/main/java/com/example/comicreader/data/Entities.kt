package com.example.comicreader.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_sessions")
data class ReadingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val comicUri: String,
    val startTime: Long,
    val endTime: Long,
    val pagesRead: Int
)

@Entity(tableName = "comic_metadata")
data class ComicMetadata(
    @PrimaryKey val uri: String,
    val hash: String,
    val fileName: String,
    val fileSize: Long,
    val lastModified: Long
)
