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
import androidx.compose.material3.Card
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
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanager.workers.RetryWorker
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import java.util.concurrent.TimeUnit

@Composable
fun RetryWorkScreen() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    var workId by remember { mutableStateOf<UUID?>(null) }

    val workInfo by remember(workManager, workId) {
        val id = workId
        if (id == null) flowOf(null)
        else workManager.getWorkInfoByIdFlow(id)
    }.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Retry & Backoff",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Worker fails intentionally for first 2 attempts, succeeds on 3rd. Observe retry behavior.",
            style = MaterialTheme.typography.bodySmall
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val request = OneTimeWorkRequestBuilder<RetryWorker>()
                        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                        .addTag("retry_linear")
                        .build()
                    workManager.enqueue(request)
                    workId = request.id
                }
            ) { Text("LINEAR Backoff") }

            Button(
                onClick = {
                    val request = OneTimeWorkRequestBuilder<RetryWorker>()
                        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                        .addTag("retry_exponential")
                        .build()
                    workManager.enqueue(request)
                    workId = request.id
                }
            ) { Text("EXPONENTIAL") }
        }

        workInfo?.let { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatusRow("State", info.state.name)
                    StatusRow("Run Attempt", info.runAttemptCount.toString())
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "Result.retry() tells WorkManager to reschedule",
                "setBackoffCriteria(policy, delay, unit)",
                "LINEAR: delay * attemptCount (10s, 20s, 30s...)",
                "EXPONENTIAL: delay * 2^(attemptCount-1) (10s, 20s, 40s...)",
                "Min backoff: 10 seconds, Max: 5 hours",
                "runAttemptCount tracks retry count"
            )
        )
    }
}
