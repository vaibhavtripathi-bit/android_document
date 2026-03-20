package com.example.customviews.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomFlowLayout(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val hSpacing = horizontalSpacing.roundToPx()
        val vSpacing = verticalSpacing.roundToPx()

        data class RowInfo(val placeables: MutableList<Placeable> = mutableListOf(), var width: Int = 0, var height: Int = 0)

        val rows = mutableListOf(RowInfo())
        var currentRow = rows.first()

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
            val neededWidth = if (currentRow.placeables.isEmpty()) placeable.width else currentRow.width + hSpacing + placeable.width

            if (neededWidth > constraints.maxWidth && currentRow.placeables.isNotEmpty()) {
                currentRow = RowInfo()
                rows.add(currentRow)
            }

            if (currentRow.placeables.isNotEmpty()) {
                currentRow.width += hSpacing
            }
            currentRow.placeables.add(placeable)
            currentRow.width += placeable.width
            currentRow.height = maxOf(currentRow.height, placeable.height)
        }

        val totalHeight = rows.sumOf { it.height } + (rows.size - 1).coerceAtLeast(0) * vSpacing
        val totalWidth = rows.maxOfOrNull { it.width } ?: 0

        layout(
            width = totalWidth.coerceAtMost(constraints.maxWidth),
            height = totalHeight.coerceAtMost(constraints.maxHeight)
        ) {
            var yOffset = 0
            rows.forEach { row ->
                var xOffset = 0
                row.placeables.forEach { placeable ->
                    placeable.placeRelative(xOffset, yOffset)
                    xOffset += placeable.width + hSpacing
                }
                yOffset += row.height + vSpacing
            }
        }
    }
}

@Composable
fun FlowChip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun FlowLayoutDemo() {
    val tags = remember {
        listOf(
            "Kotlin", "Jetpack Compose", "Android", "Material Design", "Canvas",
            "Custom Layout", "Animation", "State", "Coroutines", "Flow",
            "MVVM", "Clean Architecture", "Hilt", "Room", "Retrofit",
            "Navigation", "Testing", "CI/CD", "Gradle", "KSP"
        )
    }

    val selectedTags = remember { mutableStateListOf<String>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionTitle("Custom Flow Layout")
            Text(
                "Items wrap to the next row when they exceed available width. Built with the Layout composable and a custom MeasurePolicy.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            SectionTitle("Tag Cloud (tap to select)")
            CustomFlowLayout(modifier = Modifier.fillMaxWidth()) {
                tags.forEach { tag ->
                    val isSelected = tag in selectedTags
                    FlowChip(
                        text = tag,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                        textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .padding(0.dp)
                            .clickable {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            }
                    )
                }
            }
            if (selectedTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Selected: ${selectedTags.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
            }
        }

        item {
            SectionTitle("Colored Boxes (variable sizes)")
            CustomFlowLayout(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = 6.dp,
                verticalSpacing = 6.dp
            ) {
                val sizes = listOf(80, 60, 100, 45, 120, 70, 90, 55, 110, 65, 85, 50, 130, 40, 95)
                val colors = listOf(
                    Color(0xFF2196F3), Color(0xFFF44336), Color(0xFF4CAF50),
                    Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF00BCD4)
                )
                sizes.forEachIndexed { index, width ->
                    Box(
                        modifier = Modifier
                            .width(width.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors[index % colors.size]),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${width}dp", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        item {
            SectionTitle("With Different Spacing")
            Text("Tight (2dp):", style = MaterialTheme.typography.labelMedium)
            CustomFlowLayout(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = 2.dp,
                verticalSpacing = 2.dp
            ) {
                tags.take(10).forEach { FlowChip(text = it) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Wide (16dp):", style = MaterialTheme.typography.labelMedium)
            CustomFlowLayout(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = 16.dp,
                verticalSpacing = 16.dp
            ) {
                tags.take(10).forEach { FlowChip(text = it) }
            }
        }
    }
}
