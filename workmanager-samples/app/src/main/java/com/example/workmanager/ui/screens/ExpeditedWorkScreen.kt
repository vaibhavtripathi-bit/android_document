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
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.workmanager.workers.ExpeditedWorker
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

@Composable
fun ExpeditedWorkScreen() {
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
            "Expedited Work",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Runs with higher priority. On Android 12+, uses JobScheduler expedited jobs. On older versions, uses foreground service.",
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = {
                val request = OneTimeWorkRequestBuilder<ExpeditedWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .addTag("expedited")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Run Expedited (fallback: non-expedited)") }

        Button(
            onClick = {
                val request = OneTimeWorkRequestBuilder<ExpeditedWorker>()
                    .setExpedited(OutOfQuotaPolicy.DROP_WORK_REQUEST)
                    .addTag("expedited")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Run Expedited (fallback: drop)") }

        workInfo?.let { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatusRow("State", info.state.name)
                    StatusRow("Expedited", "true")
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "setExpedited() gives work higher execution priority",
                "Android 12+: uses JobScheduler expedited jobs (quota-limited)",
                "OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST: fallback to normal",
                "OutOfQuotaPolicy.DROP_WORK_REQUEST: drop if no quota",
                "Pre-12: used foreground service (now deprecated path)",
                "Cannot set constraints or initial delay on expedited work"
            )
        )
    }
}
