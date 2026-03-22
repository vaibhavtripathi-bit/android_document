package com.example.workmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.workmanager.workers.ForegroundWorker
import java.util.UUID

@Composable
fun ForegroundWorkScreen() {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }
    var workId by remember { mutableStateOf<UUID?>(null) }
    val workInfo by workId?.let { id ->
        workManager.getWorkInfoByIdFlow(id).collectAsState(initial = null)
    } ?: remember { mutableStateOf(null) }

    val progress = workInfo?.progress?.getInt("progress", 0) ?: 0

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Foreground / Long-Running Work", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Uses setForeground(ForegroundInfo) to show a persistent notification. Runs for ~10 seconds with progress updates.", style = MaterialTheme.typography.bodySmall)

        Button(
            onClick = {
                val request = OneTimeWorkRequestBuilder<ForegroundWorker>()
                    .addTag("foreground_work")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Start Long-Running Work") }

        if (workInfo?.state == WorkInfo.State.RUNNING) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Progress: $progress%", style = MaterialTheme.typography.bodyMedium)
        }

        workInfo?.let { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatusRow("State", info.state.name)
                    StatusRow("Progress", "$progress%")
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "setForeground(ForegroundInfo(id, notification)) promotes to foreground",
                "Required for work > 10 minutes (guaranteed execution)",
                "Must specify foregroundServiceType in manifest (Android 14+)",
                "ForegroundInfo(notifId, notification, serviceType)",
                "Notification channel must be created before use",
                "Worker can call setForeground() multiple times to update notification"
            )
        )
    }
}
