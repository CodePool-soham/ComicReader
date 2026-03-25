package com.example.comicreader.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.comicreader.ComicUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

class DuplicateManager(private val context: Context, private val comicDao: ComicDao) {

    suspend fun scanForDuplicates(uris: List<Uri>): Map<String, List<ComicMetadata>> {
        return withContext(Dispatchers.IO) {
            val allMetadata = mutableListOf<ComicMetadata>()
            
            for (uri in uris) {
                val metadata = getOrGenerateMetadata(uri)
                if (metadata != null) {
                    allMetadata.add(metadata)
                    comicDao.insertMetadata(metadata)
                }
            }

            allMetadata.groupBy { it.hash }
                .filter { it.value.size > 1 }
        }
    }

    private suspend fun getOrGenerateMetadata(uri: Uri): ComicMetadata? {
        return withContext(Dispatchers.IO) {
            try {
                var fileName = ""
                var fileSize = 0L
                var lastModified = 0L

                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (nameIndex != -1) fileName = cursor.getString(nameIndex)
                        if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                    }
                }

                val tempFile = File(context.cacheDir, "hash_temp_${System.currentTimeMillis()}")
                ComicUtils.copyUriToFile(context, uri, tempFile)
                val hash = calculateMD5(tempFile)
                lastModified = tempFile.lastModified() // Note: This might not be the original file's last modified
                tempFile.delete()

                ComicMetadata(uri.toString(), hash, fileName, fileSize, lastModified)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun calculateMD5(file: File): String {
        val digest = MessageDigest.getInstance("MD5")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read = input.read(buffer)
            while (read != -1) {
                digest.update(buffer, 0, read)
                read = input.read(buffer)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    suspend fun deleteFile(uriString: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(uriString)
                val deleted = context.contentResolver.delete(uri, null, null) > 0
                if (deleted) {
                    comicDao.deleteMetadataByUri(uriString)
                }
                deleted
            } catch (e: Exception) {
                false
            }
        }
    }
}
