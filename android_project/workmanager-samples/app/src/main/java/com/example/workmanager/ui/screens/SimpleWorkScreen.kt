package com.example.workmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.workmanager.workers.SimpleWorker
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

@Composable
fun SimpleWorkScreen() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    var taskName by remember { mutableStateOf("My Background Task") }
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
            "OneTimeWorkRequest",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Enqueues a simple one-time task with input data. The worker sleeps for 2s, then returns output data.",
            style = MaterialTheme.typography.bodySmall
        )

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val request = OneTimeWorkRequestBuilder<SimpleWorker>()
                    .setInputData(workDataOf(SimpleWorker.KEY_TASK_NAME to taskName))
                    .addTag("simple_work")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Enqueue Work") }

        workInfo?.let { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatusRow("State", info.state.name)
                    StatusRow("ID", info.id.toString().take(8) + "...")
                    StatusRow("Tags", info.tags.joinToString())
                    if (info.state == WorkInfo.State.SUCCEEDED) {
                        StatusRow(
                            "Output",
                            info.outputData.getString(SimpleWorker.KEY_RESULT_MESSAGE) ?: "—"
                        )
                    }
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "OneTimeWorkRequestBuilder<T>() creates a one-shot request",
                "setInputData() passes Data to the worker",
                "Worker returns Result.success(outputData)",
                "Data has a 10KB limit (MAX_DATA_BYTES)"
            )
        )
    }
}

@Composable
fun StatusRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InfoCard(title: String, points: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            points.forEach { point ->
                Text(
                    "• $point",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}
