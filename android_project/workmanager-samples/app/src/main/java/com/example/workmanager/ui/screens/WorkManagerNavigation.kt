package com.example.workmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

data class UseCaseItem(
    val route: String,
    val title: String,
    val subtitle: String,
    val category: String
)

val useCases = listOf(
    UseCaseItem("simple", "Simple OneTimeWork", "Basic one-time task with input/output data", "Basics"),
    UseCaseItem("periodic", "Periodic Work", "Recurring sync every 15 min with flex interval", "Basics"),
    UseCaseItem("constraints", "Constrained Work", "Network, charging, battery, storage, idle constraints", "Constraints"),
    UseCaseItem("chain", "Work Chaining", "Sequential (then) + parallel (beginWith) + combine", "Chaining"),
    UseCaseItem("unique", "Unique Work", "KEEP / REPLACE / APPEND conflict policies", "Policies"),
    UseCaseItem("retry", "Retry & Backoff", "LINEAR vs EXPONENTIAL backoff, Result.retry()", "Error Handling"),
    UseCaseItem("expedited", "Expedited Work", "setExpedited() for urgent tasks", "Priority"),
    UseCaseItem("foreground", "Foreground / Long-Running", "setForeground() with notification", "Priority"),
    UseCaseItem("progress", "Progress Reporting", "setProgress() observed in UI", "Observation"),
    UseCaseItem("inputoutput", "Input/Output Data", "Pass data between chained workers", "Data"),
    UseCaseItem("observe", "Observe Work Status", "WorkInfo states, LiveData, Flow", "Observation"),
    UseCaseItem("cancel", "Cancellation", "cancelById, cancelUnique, cancelByTag", "Lifecycle"),
    UseCaseItem("failure", "Failure Handling", "Result.failure() and error propagation", "Error Handling"),
    UseCaseItem("logs", "Work Logs", "View all worker execution logs from Room DB", "Debug")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkManagerApp() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = useCases.find { it.route == currentRoute }?.title
                            ?: "WorkManager Samples"
                    )
                },
                navigationIcon = {
                    if (currentRoute != null && currentRoute != "home") {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("simple") { SimpleWorkScreen() }
            composable("periodic") { PeriodicWorkScreen() }
            composable("constraints") { ConstrainedWorkScreen() }
            composable("chain") { ChainWorkScreen() }
            composable("unique") { UniqueWorkScreen() }
            composable("retry") { RetryWorkScreen() }
            composable("expedited") { ExpeditedWorkScreen() }
            composable("foreground") { ForegroundWorkScreen() }
            composable("progress") { ProgressWorkScreen() }
            composable("inputoutput") { InputOutputScreen() }
            composable("observe") { ObserveWorkScreen() }
            composable("cancel") { CancelWorkScreen() }
            composable("failure") { FailureWorkScreen() }
            composable("logs") { WorkLogsScreen() }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    val grouped = useCases.groupBy { it.category }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        grouped.forEach { (category, items) ->
            item {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate(item.route) },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(text = item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
