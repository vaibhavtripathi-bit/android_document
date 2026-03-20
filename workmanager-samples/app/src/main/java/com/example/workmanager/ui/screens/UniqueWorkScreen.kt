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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.workmanager.workers.SimpleWorker

@Composable
fun UniqueWorkScreen() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    val uniqueName = "unique_demo_work"
    val workInfos by workManager
        .getWorkInfosForUniqueWorkFlow(uniqueName)
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Unique Work",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "enqueueUniqueWork() ensures only one instance of named work exists. Try different policies:",
            style = MaterialTheme.typography.bodySmall
        )

        listOf(
            "KEEP" to ExistingWorkPolicy.KEEP,
            "REPLACE" to ExistingWorkPolicy.REPLACE,
            "APPEND" to ExistingWorkPolicy.APPEND,
            "APPEND_OR_REPLACE" to ExistingWorkPolicy.APPEND_OR_REPLACE
        ).forEach { (label, policy) ->
            Button(
                onClick = {
                    val request = OneTimeWorkRequestBuilder<SimpleWorker>()
                        .setInputData(workDataOf(SimpleWorker.KEY_TASK_NAME to "Unique ($label)"))
                        .build()
                    workManager.enqueueUniqueWork(uniqueName, policy, request)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Enqueue with $label") }
        }

        OutlinedButton(
            onClick = { workManager.cancelUniqueWork(uniqueName) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cancel Unique Work") }

        Text("Active Work Infos: ${workInfos.size}", style = MaterialTheme.typography.titleSmall)
        workInfos.forEachIndexed { i, info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    StatusRow("Work #${i + 1}", info.state.name)
                    StatusRow("ID", info.id.toString().take(8) + "...")
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "KEEP: if work exists & not finished, keep it, discard new",
                "REPLACE: cancel existing, enqueue new",
                "APPEND: new work runs after existing finishes",
                "APPEND_OR_REPLACE: like APPEND, but if existing failed/cancelled, replace instead"
            )
        )
    }
}
