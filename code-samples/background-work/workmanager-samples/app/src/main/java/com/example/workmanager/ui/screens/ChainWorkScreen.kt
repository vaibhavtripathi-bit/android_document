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
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.workmanager.workers.ChainWorkerA
import com.example.workmanager.workers.ChainWorkerB
import com.example.workmanager.workers.ChainWorkerC
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

@Composable
fun ChainWorkScreen() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    var chainIds by remember { mutableStateOf<List<UUID>>(emptyList()) }

    val chainWorkInfos by remember(workManager, chainIds) {
        if (chainIds.isEmpty()) {
            flowOf(emptyList<WorkInfo>())
        } else {
            combine(*chainIds.map { workManager.getWorkInfoByIdFlow(it) }.toTypedArray()) { infos ->
                infos.toList()
            }
        }
    }.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Work Chaining",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Chain A → B → C sequentially. A downloads, B processes, C uploads. Data flows through the chain.",
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = {
                val a = OneTimeWorkRequestBuilder<ChainWorkerA>().addTag("chain").build()
                val b = OneTimeWorkRequestBuilder<ChainWorkerB>().addTag("chain").build()
                val c = OneTimeWorkRequestBuilder<ChainWorkerC>().addTag("chain").build()
                workManager.beginWith(a).then(b).then(c).enqueue()
                chainIds = listOf(a.id, b.id, c.id)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Start Sequential Chain (A → B → C)") }

        Button(
            onClick = {
                val a1 = OneTimeWorkRequestBuilder<ChainWorkerA>().addTag("parallel").build()
                val a2 = OneTimeWorkRequestBuilder<ChainWorkerB>().addTag("parallel").build()
                val c = OneTimeWorkRequestBuilder<ChainWorkerC>().addTag("parallel").build()
                workManager.beginWith(listOf(a1, a2)).then(c).enqueue()
                chainIds = listOf(a1.id, a2.id, c.id)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Start Parallel → Combine ([A, B] → C)") }

        chainWorkInfos.forEachIndexed { index, info ->
            val label = when (index) {
                0 -> "Worker A"
                1 -> "Worker B"
                2 -> "Worker C"
                else -> "Worker $index"
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    StatusRow(label, info.state.name)
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "beginWith(request).then(next).then(final).enqueue()",
                "beginWith(listOf(a, b)).then(c) — parallel then combine",
                "Output data from one worker becomes input for the next",
                "If any worker fails, the chain stops (dependent workers CANCELLED)",
                "WorkContinuation can be combined with combine()"
            )
        )
    }
}
