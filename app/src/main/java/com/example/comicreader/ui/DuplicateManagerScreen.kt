package com.example.comicreader.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comicreader.data.ComicMetadata
import com.example.comicreader.data.DuplicateManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicateManagerScreen(duplicateManager: DuplicateManager, currentUris: List<android.net.Uri>) {
    var duplicates by remember { mutableStateOf<Map<String, List<ComicMetadata>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        duplicates = duplicateManager.scanForDuplicates(currentUris)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Duplicate Finder") })
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (duplicates.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No duplicates found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                duplicates.forEach { (hash, files) ->
                    item {
                        DuplicateGroupCard(files) { uri ->
                            scope.launch {
                                if (duplicateManager.deleteFile(uri)) {
                                    duplicates = duplicateManager.scanForDuplicates(currentUris)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DuplicateGroupCard(files: List<ComicMetadata>, onDelete: (String) -> Unit) {
    var showConfirmDialog by remember { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Duplicate Group", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            files.forEach { file ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(file.fileName, fontSize = 14.sp)
                        Text(
                            "${file.fileSize / 1024} KB • Modified: ${formatTimestamp(file.lastModified)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = { showConfirmDialog = file.uri }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }

    showConfirmDialog?.let { uri ->
        AlertDialog(
            onDismissRequest = { showConfirmDialog = null },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this file? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(uri)
                    showConfirmDialog = null
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
