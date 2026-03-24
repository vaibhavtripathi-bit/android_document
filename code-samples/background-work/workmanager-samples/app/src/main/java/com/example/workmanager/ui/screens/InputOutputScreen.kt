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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.example.workmanager.workers.DataProcessingWorker
import java.util.UUID

@Composable
fun InputOutputScreen() {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }
    var inputText by remember { mutableStateOf("Hello WorkManager World") }
    var workIds by remember { mutableStateOf<List<UUID>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Input/Output Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Chain: uppercase → reverse → char_count. Each worker receives output from the previous.", style = MaterialTheme.typography.bodySmall)

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Input Text") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val w1 = OneTimeWorkRequestBuilder<DataProcessingWorker>()
                    .setInputData(
                        workDataOf(
                            DataProcessingWorker.KEY_INPUT_TEXT to inputText,
                            DataProcessingWorker.KEY_OPERATION to "uppercase"
                        )
                    ).addTag("data_chain").build()

                val w2 = OneTimeWorkRequestBuilder<DataProcessingWorker>()
                    .setInputData(workDataOf(DataProcessingWorker.KEY_OPERATION to "reverse"))
                    .addTag("data_chain").build()

                val w3 = OneTimeWorkRequestBuilder<DataProcessingWorker>()
                    .setInputData(workDataOf(DataProcessingWorker.KEY_OPERATION to "char_count"))
                    .addTag("data_chain").build()

                workManager.beginWith(w1).then(w2).then(w3).enqueue()
                workIds = listOf(w1.id, w2.id, w3.id)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Run Chain: uppercase → reverse → char_count") }

        val labels = listOf("1. Uppercase", "2. Reverse", "3. Char Count")
        workIds.forEachIndexed { index, id ->
            key(id) {
                val info by workManager.getWorkInfoByIdFlow(id).collectAsState(initial = null)
                info?.let {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            StatusRow(labels.getOrElse(index) { "Worker $index" }, it.state.name)
                            if (it.state == WorkInfo.State.SUCCEEDED) {
                                val output = it.outputData.getString(DataProcessingWorker.KEY_OUTPUT_TEXT) ?: "—"
                                StatusRow("Output", output)
                            }
                        }
                    }
                }
            }
        }

        InfoCard(
            "Key Concepts",
            listOf(
                "Output data from worker N becomes input for worker N+1",
                "In chains, WorkManager merges output → input automatically",
                "Data has 10KB limit (MAX_DATA_BYTES = 10240)",
                "Only primitive types + String + arrays allowed in Data",
                "For large data, write to file and pass file path in Data"
            )
        )
    }
}
