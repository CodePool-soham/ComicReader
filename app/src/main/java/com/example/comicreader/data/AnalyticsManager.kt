package com.example.comicreader.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

class AnalyticsManager(private val comicDao: ComicDao) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun trackSession(comicUri: String, startTime: Long, endTime: Long, pagesRead: Int) {
        if (pagesRead > 0) {
            scope.launch {
                comicDao.insertSession(
                    ReadingSession(
                        comicUri = comicUri,
                        startTime = startTime,
                        endTime = endTime,
                        pagesRead = pagesRead
                    )
                )
            }
        }
    }

    fun getAnalytics(): Flow<AnalyticsData> {
        return comicDao.getAllSessions().map { sessions ->
            if (sessions.isEmpty()) return@map AnalyticsData()

            val totalComicsRead = sessions.map { it.comicUri }.distinct().size
            val totalPagesRead = sessions.sumOf { it.pagesRead }
            val averageDuration = sessions.map { it.endTime - it.startTime }.average() / 60000 // in minutes
            
            val timeOfDayCounts = mutableMapOf<Int, Int>() // 0-23 hour
            sessions.forEach {
                val cal = Calendar.getInstance().apply { timeInMillis = it.startTime }
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                timeOfDayCounts[hour] = timeOfDayCounts.getOrDefault(hour, 0) + 1
            }
            val peakHour = timeOfDayCounts.maxByOrNull { it.value }?.key ?: -1

            val dayOfWeekCounts = mutableMapOf<Int, Int>() // Calendar.DAY_OF_WEEK
            sessions.forEach {
                val cal = Calendar.getInstance().apply { timeInMillis = it.startTime }
                val day = cal.get(Calendar.DAY_OF_WEEK)
                dayOfWeekCounts[day] = dayOfWeekCounts.getOrDefault(day, 0) + 1
            }
            val preferredDay = dayOfWeekCounts.maxByOrNull { it.value }?.key ?: -1

            AnalyticsData(
                totalComicsRead = totalComicsRead,
                totalPagesRead = totalPagesRead,
                averageSessionDurationMinutes = averageDuration.toInt(),
                peakReadingHour = peakHour,
                preferredReadingDay = preferredDay,
                readingStreak = calculateStreak(sessions)
            )
        }
    }

    private fun calculateStreak(sessions: List<ReadingSession>): Int {
        if (sessions.isEmpty()) return 0
        val days = sessions.map {
            val cal = Calendar.getInstance().apply { timeInMillis = it.startTime }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.distinct().sortedDescending()

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val yesterday = today - 86400000

        if (days.first() < yesterday) return 0
        
        var streak = 0
        var checkDay = if (days.first() == today) today else yesterday

        for (day in days) {
            if (day == checkDay) {
                streak++
                checkDay -= 86400000
            } else if (day < checkDay) {
                break
            }
        }
        return streak
    }
}

data class AnalyticsData(
    val totalComicsRead: Int = 0,
    val totalPagesRead: Int = 0,
    val averageSessionDurationMinutes: Int = 0,
    val peakReadingHour: Int = -1,
    val preferredReadingDay: Int = -1,
    val readingStreak: Int = 0
)
