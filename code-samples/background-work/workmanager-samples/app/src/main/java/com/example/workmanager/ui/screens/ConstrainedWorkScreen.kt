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
import androidx.compose.material3.Switch
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
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.workmanager.workers.ConstrainedWorker
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

@Composable
fun ConstrainedWorkScreen() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    var workId by remember { mutableStateOf<UUID?>(null) }

    val workInfo by remember(workManager, workId) {
        val id = workId
        if (id == null) flowOf(null)
        else workManager.getWorkInfoByIdFlow(id)
    }.collectAsState(initial = null)

    var requireNetwork by remember { mutableStateOf(false) }
    var requireCharging by remember { mutableStateOf(false) }
    var requireBatteryNotLow by remember { mutableStateOf(false) }
    var requireStorageNotLow by remember { mutableStateOf(false) }
    var requireDeviceIdle by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Constrained Work",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Work stays ENQUEUED until all selected constraints are satisfied.",
            style = MaterialTheme.typography.bodySmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ConstraintSwitch("Require Network (Connected)", requireNetwork) { requireNetwork = it }
                ConstraintSwitch("Require Charging", requireCharging) { requireCharging = it }
                ConstraintSwitch("Battery Not Low", requireBatteryNotLow) { requireBatteryNotLow = it }
                ConstraintSwitch("Storage Not Low", requireStorageNotLow) { requireStorageNotLow = it }
                ConstraintSwitch("Device Idle", requireDeviceIdle) { requireDeviceIdle = it }
            }
        }

        Button(
            onClick = {
                val constraints = Constraints.Builder().apply {
                    if (requireNetwork) setRequiredNetworkType(NetworkType.CONNECTED)
                    setRequiresCharging(requireCharging)
                    setRequiresBatteryNotLow(requireBatteryNotLow)
                    setRequiresStorageNotLow(requireStorageNotLow)
                    setRequiresDeviceIdle(requireDeviceIdle)
                }.build()

                val desc = buildList {
                    if (requireNetwork) add("Network")
                    if (requireCharging) add("Charging")
                    if (requireBatteryNotLow) add("Battery")
                    if (requireStorageNotLow) add("Storage")
                    if (requireDeviceIdle) add("Idle")
                }.joinToString(", ").ifEmpty { "None" }

                val request = OneTimeWorkRequestBuilder<ConstrainedWorker>()
                    .setConstraints(constraints)
                    .setInputData(workDataOf(ConstrainedWorker.KEY_CONSTRAINT_TYPE to desc))
                    .addTag("constrained_work")
                    .build()
                workManager.enqueue(request)
                workId = request.id
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Enqueue Constrained Work") }

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
                "Constraints.Builder() sets preconditions",
                "NetworkType: NOT_REQUIRED, CONNECTED, UNMETERED, NOT_ROAMING, METERED, TEMPORARILY_UNMETERED",
                "setRequiresCharging(true) — only when plugged in",
                "setRequiresBatteryNotLow(true) — battery > critical",
                "setRequiresDeviceIdle(true) — device in idle (API 23+)",
                "Work stays ENQUEUED until ALL constraints are met"
            )
        )
    }
}

@Composable
fun ConstraintSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
