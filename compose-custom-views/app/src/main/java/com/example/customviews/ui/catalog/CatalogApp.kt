package com.example.customviews.ui.catalog

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.customviews.ui.views.*

enum class Difficulty(val label: String, val ordinalIndex: Int) {
    SIMPLE("Simple", 0),
    BASIC("Basic", 1),
    INTERMEDIATE("Intermediate", 2),
    ADVANCED("Advanced", 3),
    COMPLEX("Complex", 4),
    EXPERT("Expert", 5)
}

data class ViewItem(
    val route: String,
    val title: String,
    val subtitle: String,
    val difficulty: Difficulty,
    val concepts: List<String>
)

val catalogItems = listOf(
    ViewItem("badge", "Custom Badge", "Shape, color, text overlay", Difficulty.SIMPLE, listOf("Box", "Surface", "Shape", "Text")),
    ViewItem("toggle", "Animated Toggle", "State-driven animation", Difficulty.BASIC, listOf("animateColorAsState", "animateDpAsState", "Clickable")),
    ViewItem("rating", "Star Rating Bar", "Canvas drawing + touch", Difficulty.INTERMEDIATE, listOf("Canvas", "drawPath", "pointerInput", "Drag")),
    ViewItem("progress", "Circular Progress", "Gradient arc + animation", Difficulty.INTERMEDIATE, listOf("Canvas", "drawArc", "SweepGradient", "Animatable")),
    ViewItem("chart", "Bar Chart", "Data visualization", Difficulty.ADVANCED, listOf("Canvas", "drawRect", "Touch highlight", "AnimatedContent")),
    ViewItem("colorpicker", "Color Picker Wheel", "HSV math + drag gesture", Difficulty.ADVANCED, listOf("Canvas", "HSV", "pointerInput", "Drag on circle")),
    ViewItem("signature", "Signature Pad", "Path drawing + undo/redo", Difficulty.COMPLEX, listOf("Canvas", "Path", "pointerInput", "Bitmap export")),
    ViewItem("gauge", "Animated Gauge", "Speedometer with needle", Difficulty.COMPLEX, listOf("Canvas", "rotate", "Gradient segments", "Spring animation")),
    ViewItem("flowlayout", "Flow Layout", "Custom Layout composable", Difficulty.EXPERT, listOf("Layout", "MeasurePolicy", "Constraints", "Intrinsics")),
    ViewItem("nodegraph", "Node Graph Editor", "Drag-connect with Bezier", Difficulty.EXPERT, listOf("Modifier.Node", "pointerInput", "cubicTo", "State management"))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogApp() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = catalogItems.find { it.route == currentRoute }?.title
                            ?: "Compose Custom Views"
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
            composable("home") { CatalogHome(navController) }
            composable("badge") { BadgeDemo() }
            composable("toggle") { AnimatedToggleDemo() }
            composable("rating") { StarRatingDemo() }
            composable("progress") { CircularProgressDemo() }
            composable("chart") { BarChartDemo() }
            composable("colorpicker") { ColorPickerDemo() }
            composable("signature") { SignaturePadDemo() }
            composable("gauge") { AnimatedGaugeDemo() }
            composable("flowlayout") { FlowLayoutDemo() }
            composable("nodegraph") { NodeGraphDemo() }
        }
    }
}

@Composable
fun CatalogHome(navController: NavHostController) {
    val grouped = catalogItems.groupBy { it.difficulty }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        grouped.forEach { (difficulty, items) ->
            item {
                Text(
                    text = difficulty.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = if (difficulty.ordinalIndex > 0) 8.dp else 0.dp, bottom = 4.dp)
                )
            }
            items(items) { item ->
                CatalogCard(item = item, onClick = { navController.navigate(item.route) })
            }
        }
    }
}

@Composable
fun CatalogCard(item: ViewItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item.concepts.forEach { concept ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                    ) {
                        Text(
                            text = concept,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}
