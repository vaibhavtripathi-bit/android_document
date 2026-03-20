package com.example.customviews.ui.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 56.dp,
    height: Dp = 30.dp,
    thumbSize: Dp = 24.dp,
    checkedTrackColor: Color = Color(0xFF4CAF50),
    uncheckedTrackColor: Color = Color(0xFFBDBDBD),
    thumbColor: Color = Color.White
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) width - thumbSize - 4.dp else 3.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "thumb_offset"
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) checkedTrackColor else uncheckedTrackColor,
        animationSpec = tween(durationMillis = 300),
        label = "track_color"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .align(Alignment.CenterStart)
                .size(thumbSize)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

@Composable
fun LabeledToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedTrackColor: Color = Color(0xFF4CAF50),
    uncheckedTrackColor: Color = Color(0xFFBDBDBD)
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        AnimatedToggle(
            checked = checked,
            onCheckedChange = onCheckedChange,
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = uncheckedTrackColor
        )
    }
}

@Composable
fun AnimatedToggleDemo() {
    var wifi by remember { mutableStateOf(true) }
    var bluetooth by remember { mutableStateOf(false) }
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var largeToggle by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionTitle("Settings-style Toggles")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LabeledToggle("Wi-Fi", wifi, { wifi = it })
                    HorizontalDivider()
                    LabeledToggle("Bluetooth", bluetooth, { bluetooth = it }, checkedTrackColor = Color(0xFF2196F3))
                    HorizontalDivider()
                    LabeledToggle("Dark Mode", darkMode, { darkMode = it }, checkedTrackColor = Color(0xFF9C27B0))
                    HorizontalDivider()
                    LabeledToggle("Notifications", notifications, { notifications = it }, checkedTrackColor = Color(0xFFFF9800))
                }
            }
        }

        item {
            SectionTitle("Size Variants")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Small", style = MaterialTheme.typography.labelSmall)
                    AnimatedToggle(checked = wifi, onCheckedChange = { wifi = it }, width = 40.dp, height = 22.dp, thumbSize = 18.dp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Default", style = MaterialTheme.typography.labelSmall)
                    AnimatedToggle(checked = bluetooth, onCheckedChange = { bluetooth = it })
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Large", style = MaterialTheme.typography.labelSmall)
                    AnimatedToggle(checked = largeToggle, onCheckedChange = { largeToggle = it }, width = 72.dp, height = 38.dp, thumbSize = 32.dp)
                }
            }
        }

        item {
            SectionTitle("Color Variants")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    Color(0xFF4CAF50),
                    Color(0xFF2196F3),
                    Color(0xFFF44336),
                    Color(0xFFFF9800),
                    Color(0xFF9C27B0)
                ).forEachIndexed { index, color ->
                    var on by remember { mutableStateOf(index % 2 == 0) }
                    AnimatedToggle(checked = on, onCheckedChange = { on = it }, checkedTrackColor = color)
                }
            }
        }
    }
}
