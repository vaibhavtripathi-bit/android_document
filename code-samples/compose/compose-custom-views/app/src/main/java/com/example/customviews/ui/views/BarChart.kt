package com.example.customviews.ui.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class BarData(
    val label: String,
    val value: Float,
    val color: Color = Color(0xFF2196F3)
)

@Composable
fun BarChart(
    data: List<BarData>,
    modifier: Modifier = Modifier,
    barSpacing: Float = 0.3f,
    cornerRadius: Float = 8f,
    showValues: Boolean = true,
    animationDuration: Int = 800
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    if (data.isEmpty()) {
        Spacer(modifier.fillMaxWidth().height(250.dp))
        return
    }

    val maxValue = data.maxOfOrNull { it.value } ?: 1f

    val animatables = remember(data) {
        List(data.size) { Animatable(0f) }
    }

    LaunchedEffect(data, animationDuration) {
        coroutineScope {
            animatables.forEach { it.snapTo(0f) }
            animatables.forEachIndexed { index, anim ->
                launch {
                    delay(index * 80L)
                    anim.animateTo(
                        1f,
                        tween(durationMillis = animationDuration, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    val animatedFractions = animatables.map { it.value }

    val density = LocalDensity.current
    val labelTextSize = with(density) { 11.sp.toPx() }
    val valueTextSize = with(density) { 10.sp.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .pointerInput(data.size) {
                detectTapGestures { offset ->
                    val barWidth = size.width.toFloat() / data.size
                    val tappedIndex = (offset.x / barWidth).toInt().coerceIn(0, data.size - 1)
                    selectedIndex = if (selectedIndex == tappedIndex) -1 else tappedIndex
                }
            }
    ) {
        val chartHeight = size.height - labelTextSize * 2.5f
        val barTotalWidth = size.width / data.size
        val barWidth = barTotalWidth * (1f - barSpacing)
        val barOffset = barTotalWidth * barSpacing / 2f

        data.forEachIndexed { index, bar ->
            val fraction = animatedFractions.getOrElse(index) { 1f }
            val barHeight = (bar.value / maxValue) * chartHeight * fraction
            val x = index * barTotalWidth + barOffset
            val y = chartHeight - barHeight

            val barColor = if (index == selectedIndex) bar.color.copy(alpha = 0.7f) else bar.color

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )

            if (index == selectedIndex) {
                drawRoundRect(
                    color = bar.color.copy(alpha = 0.15f),
                    topLeft = Offset(x - 4f, y - 4f),
                    size = Size(barWidth + 8f, barHeight + 4f),
                    cornerRadius = CornerRadius(cornerRadius + 2, cornerRadius + 2)
                )
            }

            drawContext.canvas.nativeCanvas.apply {
                val labelPaint = android.graphics.Paint().apply {
                    textSize = labelTextSize
                    textAlign = android.graphics.Paint.Align.CENTER
                    color = android.graphics.Color.DKGRAY
                    isAntiAlias = true
                }
                drawText(bar.label, x + barWidth / 2, size.height, labelPaint)

                if (showValues) {
                    val valuePaint = android.graphics.Paint().apply {
                        textSize = valueTextSize
                        textAlign = android.graphics.Paint.Align.CENTER
                        color = android.graphics.Color.DKGRAY
                        isFakeBoldText = true
                        isAntiAlias = true
                    }
                    drawText("${bar.value.toInt()}", x + barWidth / 2, y - 8f, valuePaint)
                }
            }
        }
    }
}

@Composable
fun BarChartDemo() {
    val salesData = remember {
        listOf(
            BarData("Jan", 120f, Color(0xFF2196F3)),
            BarData("Feb", 85f, Color(0xFF03A9F4)),
            BarData("Mar", 200f, Color(0xFF00BCD4)),
            BarData("Apr", 160f, Color(0xFF009688)),
            BarData("May", 240f, Color(0xFF4CAF50)),
            BarData("Jun", 180f, Color(0xFF8BC34A)),
            BarData("Jul", 300f, Color(0xFFCDDC39))
        )
    }

    val languageData = remember {
        listOf(
            BarData("Kotlin", 92f, Color(0xFF7C4DFF)),
            BarData("Java", 68f, Color(0xFFF44336)),
            BarData("Swift", 55f, Color(0xFFFF9800)),
            BarData("Dart", 42f, Color(0xFF00BCD4)),
            BarData("C++", 38f, Color(0xFF607D8B))
        )
    }

    var animKey by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionTitle("Monthly Sales (tap a bar)")
            key(animKey) {
                BarChart(data = salesData)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { animKey++ }) { Text("Replay Animation") }
        }

        item {
            SectionTitle("Language Popularity")
            key(animKey) {
                BarChart(data = languageData, cornerRadius = 16f)
            }
        }

        item {
            SectionTitle("Uniform Color")
            key(animKey) {
                BarChart(
                    data = listOf(
                        BarData("Q1", 45f, Color(0xFF9C27B0)),
                        BarData("Q2", 78f, Color(0xFF9C27B0)),
                        BarData("Q3", 62f, Color(0xFF9C27B0)),
                        BarData("Q4", 95f, Color(0xFF9C27B0))
                    )
                )
            }
        }
    }
}
