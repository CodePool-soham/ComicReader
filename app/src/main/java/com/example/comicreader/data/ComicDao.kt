package com.example.comicreader.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ReadingSession)

    @Query("SELECT * FROM reading_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<ReadingSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: ComicMetadata)

    @Query("SELECT * FROM comic_metadata")
    suspend fun getAllMetadata(): List<ComicMetadata>

    @Query("DELETE FROM comic_metadata WHERE uri = :uri")
    suspend fun deleteMetadataByUri(uri: String)

    @Query("SELECT * FROM comic_metadata WHERE hash = :hash")
    suspend fun getMetadataByHash(hash: String): List<ComicMetadata>
}
