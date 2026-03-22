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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanager.workers.PeriodicSyncWorker
import java.util.concurrent.TimeUnit

@Composable
fun PeriodicWorkScreen() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    val workInfos by workManager
        .getWorkInfosForUniqueWorkFlow(PeriodicSyncWorker.UNIQUE_NAME)
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "PeriodicWorkRequest",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Schedules recurring work every 15 minutes with a 5-minute flex window. Uses ExistingPeriodicWorkPolicy.KEEP to avoid duplicates.",
            style = MaterialTheme.typography.bodySmall
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val request = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(
                        15,
                        TimeUnit.MINUTES,
                        5,
                        TimeUnit.MINUTES
                    )
                        .addTag("periodic_sync")
                        .build()
                    workManager.enqueueUniquePeriodicWork(
                        PeriodicSyncWorker.UNIQUE_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        request
                    )
                }
            ) { Text("Start Sync") }

            OutlinedButton(
                onClick = {
                    workManager.cancelUniqueWork(PeriodicSyncWorker.UNIQUE_NAME)
                }
            ) { Text("Stop Sync") }
        }

        workInfos.forEach { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatusRow("State", info.state.name)
                    StatusRow("Run Attempt", info.runAttemptCount.toString())
                    StatusRow("ID", info.id.toString().take(8) + "...")
                    StatusRow("Periodic", "true")
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "PeriodicWorkRequestBuilder(repeatInterval, flexInterval)",
                "Minimum repeat interval: 15 minutes",
                "Flex interval: work runs within [repeat - flex, repeat]",
                "enqueueUniquePeriodicWork() prevents duplicates",
                "ExistingPeriodicWorkPolicy: KEEP, UPDATE, CANCEL_AND_REENQUEUE"
            )
        )
    }
}
