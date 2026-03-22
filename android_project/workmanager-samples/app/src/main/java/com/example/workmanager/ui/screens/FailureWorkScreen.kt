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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.workmanager.workers.FailingWorker
import java.util.UUID

@Composable
fun FailureWorkScreen() {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }
    var workId by remember { mutableStateOf<UUID?>(null) }
    val workInfo by workId?.let { id ->
        workManager.getWorkInfoByIdFlow(id).collectAsState(initial = null)
    } ?: remember { mutableStateOf(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Failure Handling", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Demonstrates Result.failure() with error data. Compare with Result.success().", style = MaterialTheme.typography.bodySmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val request = OneTimeWorkRequestBuilder<FailingWorker>()
                    .setInputData(workDataOf(FailingWorker.KEY_SHOULD_FAIL to true))
                    .addTag("failure_demo")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            }) { Text("Run (will FAIL)") }

            OutlinedButton(onClick = {
                val request = OneTimeWorkRequestBuilder<FailingWorker>()
                    .setInputData(workDataOf(FailingWorker.KEY_SHOULD_FAIL to false))
                    .addTag("failure_demo")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            }) { Text("Run (will SUCCEED)") }
        }

        workInfo?.let { info ->
            val cardColor = when (info.state) {
                WorkInfo.State.FAILED -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                WorkInfo.State.SUCCEEDED -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                else -> CardDefaults.cardColors()
            }
            Card(modifier = Modifier.fillMaxWidth(), colors = cardColor) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatusRow("State", info.state.name)
                    if (info.state == WorkInfo.State.FAILED) {
                        val errorMsg = info.outputData.getString(FailingWorker.KEY_ERROR_MESSAGE) ?: "Unknown"
                        StatusRow("Error", errorMsg)
                    }
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "Result.failure() marks work as permanently failed",
                "Result.failure(outputData) can carry error details",
                "Failed work in a chain causes dependents to be CANCELLED",
                "Result.retry() vs Result.failure(): retry is temporary, failure is terminal",
                "Periodic work: failure doesn't stop future executions",
                "OneTime work: FAILED is a terminal state"
            )
        )
    }
}
