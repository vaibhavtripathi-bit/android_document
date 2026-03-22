package com.example.workmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.workmanager.data.WorkLog
import com.example.workmanager.data.WorkLogDao
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkLogDaoEntryPoint {
    fun workLogDao(): WorkLogDao
}

@Composable
fun WorkLogsScreen() {
    val context = LocalContext.current
    val dao = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            WorkLogDaoEntryPoint::class.java
        ).workLogDao()
    }
    val logs by dao.getRecentLogs().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Work Logs (${logs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = { scope.launch { dao.clearAll() } }) {
                Text("Clear All")
            }
        }

        if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp)) {
                Text(
                    "No logs yet. Run some workers!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(logs) { log ->
                    LogItem(log)
                }
            }
        }
    }
}

@Composable
fun LogItem(log: WorkLog) {
    val statusColor = when (log.status) {
        "COMPLETED" -> MaterialTheme.colorScheme.primary
        "FAILED" -> MaterialTheme.colorScheme.error
        "RUNNING", "STARTED" -> MaterialTheme.colorScheme.tertiary
        "RETRY", "ATTEMPT" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.US) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(log.workerName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(log.status, style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
            }
            Text(log.message, style = MaterialTheme.typography.bodySmall)
            Text(
                dateFormat.format(Date(log.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
