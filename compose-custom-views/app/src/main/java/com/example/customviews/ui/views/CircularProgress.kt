package com.example.customviews.ui.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GradientCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 10.dp,
    trackColor: Color = Color(0xFFE0E0E0),
    gradientColors: List<Color> = listOf(Color(0xFF2196F3), Color(0xFF00BCD4), Color(0xFF4CAF50)),
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = animationSpec,
        label = "progress_anim"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            if (animatedProgress > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(gradientColors),
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
        }

        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                fontSize = (size.value / 5).sp,
                fontWeight = FontWeight.Bold,
                color = gradientColors.first()
            )
        }
    }
}

@Composable
fun IndeterminateCircularProgress(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 6.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "indeterminate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "rotation"
    )
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = 270f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweep"
    )

    Canvas(modifier = modifier.size(size)) {
        val stroke = strokeWidth.toPx()
        val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
        val topLeft = Offset(stroke / 2, stroke / 2)

        drawArc(
            color = color.copy(alpha = 0.15f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        drawArc(
            color = color,
            startAngle = rotation,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun CircularProgressDemo() {
    var progress by remember { mutableFloatStateOf(0.72f) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SectionTitle("Gradient Circular Progress")
            GradientCircularProgress(progress = progress, size = 160.dp, strokeWidth = 14.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Slider(
                value = progress,
                onValueChange = { progress = it },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Progress: ${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
        }

        item {
            SectionTitle("Color Variants")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GradientCircularProgress(
                    progress = 0.85f,
                    size = 80.dp,
                    strokeWidth = 8.dp,
                    gradientColors = listOf(Color(0xFFF44336), Color(0xFFFF9800), Color(0xFFFFC107))
                )
                GradientCircularProgress(
                    progress = 0.6f,
                    size = 80.dp,
                    strokeWidth = 8.dp,
                    gradientColors = listOf(Color(0xFF9C27B0), Color(0xFFE91E63))
                )
                GradientCircularProgress(
                    progress = 0.45f,
                    size = 80.dp,
                    strokeWidth = 8.dp,
                    gradientColors = listOf(Color(0xFF00BCD4), Color(0xFF009688))
                )
            }
        }

        item {
            SectionTitle("Thick vs Thin Stroke")
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                GradientCircularProgress(progress = 0.7f, size = 90.dp, strokeWidth = 4.dp)
                GradientCircularProgress(progress = 0.7f, size = 90.dp, strokeWidth = 18.dp)
            }
        }

        item {
            SectionTitle("Indeterminate (Loading)")
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                IndeterminateCircularProgress(color = Color(0xFF2196F3))
                IndeterminateCircularProgress(color = Color(0xFFF44336), size = 60.dp, strokeWidth = 4.dp)
                IndeterminateCircularProgress(color = Color(0xFF4CAF50), size = 100.dp, strokeWidth = 10.dp)
            }
        }

        item {
            SectionTitle("Quick Presets")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { p ->
                    Button(onClick = { progress = p }) { Text("${(p * 100).toInt()}%") }
                }
            }
        }
    }
}
