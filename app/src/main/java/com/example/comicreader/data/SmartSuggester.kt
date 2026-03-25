package com.example.comicreader.data

import android.net.Uri
import com.example.comicreader.Comic
import java.util.regex.Pattern

object SmartSuggester {

    fun suggestNext(currentComic: Comic, allComics: List<Comic>): Comic? {
        val seriesInfo = parseSeriesInfo(currentComic.name) ?: return null
        
        return allComics.asSequence()
            .filter { it.uri != currentComic.uri }
            .mapNotNull { comic ->
                val info = parseSeriesInfo(comic.name)
                if (info != null && info.seriesName.equals(seriesInfo.seriesName, ignoreCase = true)) {
                    comic to info.issueNumber
                } else null
            }
            .filter { it.second > seriesInfo.issueNumber }
            .minByOrNull { it.second }?.first
    }

    data class SeriesInfo(val seriesName: String, val issueNumber: Float)

    private fun parseSeriesInfo(fileName: String): SeriesInfo? {
        val nameWithoutExt = fileName.substringBeforeLast(".")
        
        // Patterns to match: Batman_01, Batman-1, Batman Issue 01, Batman 01.5
        val patterns = listOf(
            Pattern.compile("^(.*?)[\\s_-]+(?:Issue|#|vol)?(?:\\s+)?(\\d+(?:\\.\\d+)?)$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^(.*?)(\\d+(?:\\.\\d+)?)$", Pattern.CASE_INSENSITIVE)
        )

        for (pattern in patterns) {
            val matcher = pattern.matcher(nameWithoutExt)
            if (matcher.find()) {
                val seriesName = matcher.group(1)?.trim() ?: ""
                val issueStr = matcher.group(2)
                val issueNumber = issueStr?.toFloatOrNull()
                if (seriesName.isNotEmpty() && issueNumber != null) {
                    return SeriesInfo(seriesName, issueNumber)
                }
            }
        }
        return null
    }
}
