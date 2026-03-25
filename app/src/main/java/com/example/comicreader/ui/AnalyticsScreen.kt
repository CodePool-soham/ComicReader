package com.example.comicreader.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comicreader.data.AnalyticsData
import com.example.comicreader.data.AnalyticsManager
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(analyticsManager: AnalyticsManager) {
    val analyticsData by analyticsManager.getAnalytics().collectAsState(initial = AnalyticsData())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Reading Analytics") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnalyticsCard("Overview", listOf(
                    "Total Comics Read" to "${analyticsData.totalComicsRead}",
                    "Total Pages Read" to "${analyticsData.totalPagesRead}",
                    "Reading Streak" to "${analyticsData.readingStreak} days"
                ))
            }

            item {
                AnalyticsCard("Habits", listOf(
                    "Avg. Session" to "${analyticsData.averageSessionDurationMinutes} min",
                    "Peak Time" to formatHour(analyticsData.peakReadingHour),
                    "Fav. Day" to formatDay(analyticsData.preferredReadingDay)
                ))
            }

            item {
                InsightsSection(analyticsData)
            }
        }
    }
}

@Composable
fun AnalyticsCard(title: String, stats: List<Pair<String, String>>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            stats.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label)
                    Text(value, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun InsightsSection(data: AnalyticsData) {
    Column {
        Text("Insights", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        if (data.peakReadingHour != -1) {
            Text("Your peak reading time is ${formatHour(data.peakReadingHour)}")
        }
        if (data.preferredReadingDay != -1) {
            val dayName = when(data.preferredReadingDay) {
                Calendar.SATURDAY, Calendar.SUNDAY -> "weekends"
                else -> "weekdays"
            }
            Text("You read more on $dayName")
        }
        if (data.readingStreak > 3) {
            Text("You're on a roll! Keep it up!")
        }
    }
}

private fun formatHour(hour: Int): String {
    if (hour == -1) return "N/A"
    return when {
        hour == 0 -> "12 AM"
        hour < 12 -> "$hour AM"
        hour == 12 -> "12 PM"
        else -> "${hour - 12} PM"
    }
}

private fun formatDay(day: Int): String {
    return when(day) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> "N/A"
    }
}
