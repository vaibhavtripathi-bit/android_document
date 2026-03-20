package com.example.workmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.work.workDataOf
import com.example.workmanager.workers.ProgressWorker
import java.util.UUID

@Composable
fun CancelWorkScreen() {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }
    var workId by remember { mutableStateOf<UUID?>(null) }
    val workInfo by workId?.let { id ->
        workManager.getWorkInfoByIdFlow(id).collectAsState(initial = null)
    } ?: remember { mutableStateOf(null) }

    val taggedInfos by workManager.getWorkInfosByTagFlow("cancellable")
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Cancellation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Start long-running work, then cancel it using different methods.", style = MaterialTheme.typography.bodySmall)

        Button(
            onClick = {
                val request = OneTimeWorkRequestBuilder<ProgressWorker>()
                    .setInputData(workDataOf(ProgressWorker.KEY_TOTAL_STEPS to 20))
                    .addTag("cancellable")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Start Work (20 steps)") }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { workId?.let { workManager.cancelWorkById(it) } },
                enabled = workInfo?.state == WorkInfo.State.RUNNING || workInfo?.state == WorkInfo.State.ENQUEUED
            ) { Text("Cancel by ID") }

            Button(onClick = {
                workManager.cancelAllWorkByTag("cancellable")
            }) { Text("Cancel by Tag") }

            Button(
                onClick = {
                    workManager.cancelAllWork()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel ALL")
            }
        }

        workInfo?.let { info ->
            val progress = info.progress.getInt(ProgressWorker.KEY_PROGRESS, 0)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatusRow("State", info.state.name)
                    StatusRow("Progress", "$progress%")
                    if (info.state == WorkInfo.State.RUNNING) {
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        Text("Tagged 'cancellable' works: ${taggedInfos.size}", style = MaterialTheme.typography.bodySmall)

        InfoCard(
            "Key Concepts",
            listOf(
                "cancelWorkById(UUID) — cancel specific work",
                "cancelUniqueWork(name) — cancel by unique name",
                "cancelAllWorkByTag(tag) — cancel all with tag",
                "cancelAllWork() — nuclear option, cancels everything",
                "Cancelled work transitions to CANCELLED state",
                "CoroutineWorker: cancellation = coroutine cancellation",
                "isStopped flag is set — check in loops for cooperative cancellation"
            )
        )
    }
}
