package com.example.customviews.ui.views

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedGauge(
    value: Float,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    maxValue: Float = 100f,
    segments: List<GaugeSegment> = defaultSegments(),
    label: String = "",
    unit: String = "",
    arcWidth: Float = 28f,
    needleColor: Color = Color(0xFF333333)
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.coerceIn(minValue, maxValue),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "gauge_value"
    )

    val startAngle = 135f
    val sweepAngle = 270f
    val fraction = (animatedValue - minValue) / (maxValue - minValue)
    val needleAngle = startAngle + sweepAngle * fraction

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val padding = arcWidth + 20f
            val arcSize = Size(size.width - padding * 2, size.height - padding * 2)
            val arcTopLeft = Offset(padding, padding)

            segments.forEach { segment ->
                val segStart = startAngle + sweepAngle * ((segment.startValue - minValue) / (maxValue - minValue))
                val segSweep = sweepAngle * ((segment.endValue - segment.startValue) / (maxValue - minValue))
                drawArc(
                    color = segment.color,
                    startAngle = segStart,
                    sweepAngle = segSweep,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = arcWidth, cap = StrokeCap.Butt)
                )
            }

            drawTickMarks(arcTopLeft, arcSize, startAngle, sweepAngle, minValue, maxValue, arcWidth)

            val center = Offset(size.width / 2, size.height / 2)
            val needleLength = arcSize.width / 2 - 10f
            val angleRad = Math.toRadians(needleAngle.toDouble())

            val needleTip = Offset(
                center.x + (needleLength * cos(angleRad)).toFloat(),
                center.y + (needleLength * sin(angleRad)).toFloat()
            )
            val needleTail = Offset(
                center.x - (20f * cos(angleRad)).toFloat(),
                center.y - (20f * sin(angleRad)).toFloat()
            )

            drawLine(needleColor, needleTail, needleTip, strokeWidth = 4f, cap = StrokeCap.Round)
            drawCircle(needleColor, radius = 10f, center = center)
            drawCircle(Color.White, radius = 5f, center = center)

            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    textSize = 36f
                    textAlign = android.graphics.Paint.Align.CENTER
                    color = android.graphics.Color.DKGRAY
                    isFakeBoldText = true
                    isAntiAlias = true
                }
                drawText(
                    "${animatedValue.toInt()}$unit",
                    center.x,
                    center.y + arcSize.height / 4,
                    textPaint
                )
                if (label.isNotEmpty()) {
                    val labelPaint = android.graphics.Paint().apply {
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                        color = android.graphics.Color.GRAY
                        isAntiAlias = true
                    }
                    drawText(label, center.x, center.y + arcSize.height / 4 + 32f, labelPaint)
                }
            }
        }
    }
}

data class GaugeSegment(
    val startValue: Float,
    val endValue: Float,
    val color: Color
)

fun defaultSegments() = listOf(
    GaugeSegment(0f, 30f, Color(0xFF4CAF50)),
    GaugeSegment(30f, 60f, Color(0xFFFFC107)),
    GaugeSegment(60f, 80f, Color(0xFFFF9800)),
    GaugeSegment(80f, 100f, Color(0xFFF44336))
)

private fun DrawScope.drawTickMarks(
    arcTopLeft: Offset,
    arcSize: Size,
    startAngle: Float,
    sweepAngle: Float,
    minValue: Float,
    maxValue: Float,
    arcWidth: Float
) {
    val center = Offset(arcTopLeft.x + arcSize.width / 2, arcTopLeft.y + arcSize.height / 2)
    val outerRadius = arcSize.width / 2 + arcWidth / 2 + 4f
    val majorTickLength = 14f
    val minorTickLength = 8f
    val steps = 10
    val minorSteps = 5

    for (i in 0..steps) {
        val angle = startAngle + sweepAngle * i / steps
        val rad = Math.toRadians(angle.toDouble())
        val outerPoint = Offset(
            center.x + (outerRadius * cos(rad)).toFloat(),
            center.y + (outerRadius * sin(rad)).toFloat()
        )
        val innerPoint = Offset(
            center.x + ((outerRadius - majorTickLength) * cos(rad)).toFloat(),
            center.y + ((outerRadius - majorTickLength) * sin(rad)).toFloat()
        )
        drawLine(Color.DarkGray, innerPoint, outerPoint, strokeWidth = 2f)

        if (i < steps) {
            for (j in 1 until minorSteps) {
                val minorAngle = startAngle + sweepAngle * (i * minorSteps + j) / (steps * minorSteps)
                val minorRad = Math.toRadians(minorAngle.toDouble())
                val mOuter = Offset(
                    center.x + (outerRadius * cos(minorRad)).toFloat(),
                    center.y + (outerRadius * sin(minorRad)).toFloat()
                )
                val mInner = Offset(
                    center.x + ((outerRadius - minorTickLength) * cos(minorRad)).toFloat(),
                    center.y + ((outerRadius - minorTickLength) * sin(minorRad)).toFloat()
                )
                drawLine(Color.Gray, mInner, mOuter, strokeWidth = 1f)
            }
        }
    }
}

@Composable
fun AnimatedGaugeDemo() {
    var speed by remember { mutableFloatStateOf(45f) }
    var temperature by remember { mutableFloatStateOf(72f) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SectionTitle("Speedometer")
            AnimatedGauge(
                value = speed,
                maxValue = 200f,
                label = "Speed",
                unit = " km/h",
                segments = listOf(
                    GaugeSegment(0f, 60f, Color(0xFF4CAF50)),
                    GaugeSegment(60f, 120f, Color(0xFFFFC107)),
                    GaugeSegment(120f, 160f, Color(0xFFFF9800)),
                    GaugeSegment(160f, 200f, Color(0xFFF44336))
                )
            )
            Slider(value = speed, onValueChange = { speed = it }, valueRange = 0f..200f, modifier = Modifier.fillMaxWidth())
        }

        item {
            SectionTitle("Temperature")
            AnimatedGauge(
                value = temperature,
                minValue = -20f,
                maxValue = 120f,
                label = "Temp",
                unit = "°",
                segments = listOf(
                    GaugeSegment(-20f, 0f, Color(0xFF2196F3)),
                    GaugeSegment(0f, 35f, Color(0xFF4CAF50)),
                    GaugeSegment(35f, 70f, Color(0xFFFFC107)),
                    GaugeSegment(70f, 100f, Color(0xFFFF9800)),
                    GaugeSegment(100f, 120f, Color(0xFFF44336))
                )
            )
            Slider(value = temperature, onValueChange = { temperature = it }, valueRange = -20f..120f, modifier = Modifier.fillMaxWidth())
        }

        item {
            SectionTitle("Quick Presets")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0f, 40f, 80f, 130f, 180f).forEach { v ->
                    Button(onClick = { speed = v }) { Text("${v.toInt()}") }
                }
            }
        }
    }
}
