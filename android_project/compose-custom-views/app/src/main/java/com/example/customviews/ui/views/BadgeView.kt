package com.example.customviews.ui.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = RoundedCornerShape(12.dp),
    fontSize: TextUnit = 12.sp,
    horizontalPadding: Dp = 10.dp,
    verticalPadding: Dp = 4.dp
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding)
        )
    }
}

@Composable
fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    maxCount: Int = 99,
    backgroundColor: Color = Color(0xFFF44336),
    contentColor: Color = Color.White
) {
    val displayText = if (count > maxCount) "$maxCount+" else "$count"
    val animatedBg by animateColorAsState(
        targetValue = if (count > 0) backgroundColor else Color.Gray,
        label = "badge_color"
    )

    Surface(
        modifier = modifier.defaultMinSize(minWidth = 24.dp, minHeight = 24.dp),
        shape = CircleShape,
        color = animatedBg,
        contentColor = contentColor
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
            Text(
                text = displayText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (status.lowercase()) {
        "active", "online" -> Color(0xFF4CAF50) to Color.White
        "inactive", "offline" -> Color(0xFF9E9E9E) to Color.White
        "warning" -> Color(0xFFFFC107) to Color.Black
        "error", "critical" -> Color(0xFFF44336) to Color.White
        "pending" -> Color(0xFF2196F3) to Color.White
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    CustomBadge(
        text = status.uppercase(),
        backgroundColor = bg,
        contentColor = fg,
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        fontSize = 10.sp,
        horizontalPadding = 8.dp,
        verticalPadding = 3.dp
    )
}

@Composable
fun BadgeDemo() {
    var count by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionTitle("Basic Badges")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomBadge(text = "NEW")
                CustomBadge(text = "SALE", backgroundColor = Color(0xFFF44336))
                CustomBadge(text = "PRO", backgroundColor = Color(0xFF9C27B0))
                CustomBadge(text = "FREE", backgroundColor = Color(0xFF4CAF50))
            }
        }

        item {
            SectionTitle("Shape Variants")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomBadge(text = "Rounded", shape = RoundedCornerShape(12.dp))
                CustomBadge(text = "Pill", shape = RoundedCornerShape(50))
                CustomBadge(text = "Square", shape = RoundedCornerShape(0.dp))
                CustomBadge(text = "Circle", shape = CircleShape)
            }
        }

        item {
            SectionTitle("Count Badge")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CountBadge(count = count)
                Button(onClick = { count++ }) { Text("+1") }
                Button(onClick = { count = 0 }) { Text("Reset") }
                OutlinedButton(onClick = { count = 150 }) { Text("Set 150") }
            }
        }

        item {
            SectionTitle("Status Badges")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Active", "Inactive", "Warning", "Error", "Pending").forEach {
                    StatusBadge(status = it)
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
