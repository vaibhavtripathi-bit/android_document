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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
fun ProgressWorkScreen() {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }
    var workId by remember { mutableStateOf<UUID?>(null) }
    val workInfo by workId?.let { id ->
        workManager.getWorkInfoByIdFlow(id).collectAsState(initial = null)
    } ?: remember { mutableStateOf(null) }

    var totalSteps by remember { mutableIntStateOf(10) }
    val progress = workInfo?.progress?.getInt(ProgressWorker.KEY_PROGRESS, 0) ?: 0
    val currentStep = workInfo?.progress?.getInt(ProgressWorker.KEY_CURRENT_STEP, 0) ?: 0

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Progress Reporting", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Worker calls setProgress() with Data. UI observes via getWorkInfoByIdFlow().", style = MaterialTheme.typography.bodySmall)

        Text("Total Steps: $totalSteps")
        Slider(
            value = totalSteps.toFloat(),
            onValueChange = { totalSteps = it.toInt() },
            valueRange = 5f..20f,
            steps = 14
        )

        Button(
            onClick = {
                val request = OneTimeWorkRequestBuilder<ProgressWorker>()
                    .setInputData(workDataOf(ProgressWorker.KEY_TOTAL_STEPS to totalSteps))
                    .addTag("progress_work")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Start Progress Work ($totalSteps steps)") }

        if (workInfo?.state == WorkInfo.State.RUNNING || progress > 0) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Step $currentStep / $totalSteps ($progress%)", style = MaterialTheme.typography.bodyMedium)
        }

        workInfo?.let { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatusRow("State", info.state.name)
                    StatusRow("Progress", "$progress%")
                    StatusRow("Step", "$currentStep / $totalSteps")
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "setProgress(Data) updates intermediate progress",
                "Progress is observable via WorkInfo.progress",
                "Only available while worker is RUNNING",
                "Progress data is cleared when work completes",
                "Use workDataOf() to create progress Data"
            )
        )
    }
}
