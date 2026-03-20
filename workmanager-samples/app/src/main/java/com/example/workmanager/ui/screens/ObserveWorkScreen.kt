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
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.workmanager.workers.SimpleWorker
import java.util.UUID

@Composable
fun ObserveWorkScreen() {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }
    var workId by remember { mutableStateOf<UUID?>(null) }
    val workInfoById by workId?.let { id ->
        workManager.getWorkInfoByIdFlow(id).collectAsState(initial = null)
    } ?: remember { mutableStateOf(null) }

    val workInfosByTag by workManager.getWorkInfosByTagFlow("observable_work")
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Observe Work Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Multiple ways to observe: by ID (Flow), by tag (Flow), by unique name (Flow).", style = MaterialTheme.typography.bodySmall)

        Button(
            onClick = {
                val request = OneTimeWorkRequestBuilder<SimpleWorker>()
                    .setInputData(workDataOf(SimpleWorker.KEY_TASK_NAME to "Observable Task"))
                    .addTag("observable_work")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Enqueue Tagged Work") }

        Text("Observe by ID", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        workInfoById?.let { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    StatusRow("State", info.state.name)
                    StatusRow("ID", info.id.toString().take(8) + "...")
                    StatusRow("Run Attempt", info.runAttemptCount.toString())
                }
            }
        }

        Text("Observe by Tag (\"observable_work\")", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("Found ${workInfosByTag.size} work items", style = MaterialTheme.typography.bodySmall)
        workInfosByTag.takeLast(5).forEach { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    StatusRow("State", info.state.name)
                    StatusRow("ID", info.id.toString().take(8) + "...")
                }
            }
        }

        InfoCard(
            "WorkInfo States",
            listOf(
                "ENQUEUED — waiting for constraints / scheduling",
                "RUNNING — currently executing",
                "SUCCEEDED — completed successfully (terminal for OneTime)",
                "FAILED — returned Result.failure() (terminal for OneTime)",
                "BLOCKED — waiting for prerequisite in chain",
                "CANCELLED — cancelled by user (terminal)"
            )
        )

        InfoCard(
            "Observation APIs",
            listOf(
                "getWorkInfoByIdFlow(UUID): Flow<WorkInfo?>",
                "getWorkInfosByTagFlow(tag): Flow<List<WorkInfo>>",
                "getWorkInfosForUniqueWorkFlow(name): Flow<List<WorkInfo>>",
                "LiveData variants also available (getWorkInfoByIdLiveData, etc.)",
                "WorkQuery for complex queries (by state, tag, id, unique name)"
            )
        )
    }
}
