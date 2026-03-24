package com.example.customviews.ui.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

private fun createStarPath(cx: Float, cy: Float, outerRadius: Float, innerRadius: Float): Path {
    val path = Path()
    val angleStep = Math.PI / 5
    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = angleStep * i - Math.PI / 2
        val x = cx + (radius * cos(angle)).toFloat()
        val y = cy + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    fillFraction: Float,
    filledColor: Color,
    emptyColor: Color
) {
    val starPath = createStarPath(center.x, center.y, outerRadius, innerRadius)
    drawPath(starPath, emptyColor)

    if (fillFraction > 0f) {
        clipRect(
            left = center.x - outerRadius,
            top = center.y - outerRadius,
            right = center.x - outerRadius + outerRadius * 2 * fillFraction,
            bottom = center.y + outerRadius
        ) {
            drawPath(starPath, filledColor)
        }
    }
}

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    allowHalf: Boolean = true,
    starSize: Dp = 40.dp,
    spacing: Dp = 4.dp,
    filledColor: Color = Color(0xFFFFC107),
    emptyColor: Color = Color(0xFFE0E0E0),
    enabled: Boolean = true
) {
    val animatedRating by animateFloatAsState(
        targetValue = rating,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "rating_anim"
    )

    val totalWidth = starSize * maxStars + spacing * (maxStars - 1)

    Canvas(
        modifier = modifier
            .width(totalWidth)
            .height(starSize)
            .then(
                if (enabled) {
                    Modifier
                        .pointerInput(maxStars, allowHalf) {
                            detectTapGestures { offset ->
                                val newRating = computeRating(offset.x, size.width.toFloat(), maxStars, allowHalf)
                                onRatingChange(newRating)
                            }
                        }
                        .pointerInput(maxStars, allowHalf) {
                            detectHorizontalDragGestures { change, _ ->
                                change.consume()
                                val newRating = computeRating(change.position.x, size.width.toFloat(), maxStars, allowHalf)
                                onRatingChange(newRating)
                            }
                        }
                } else Modifier
            )
    ) {
        val starSizePx = starSize.toPx()
        val spacingPx = spacing.toPx()
        val outerRadius = starSizePx / 2f
        val innerRadius = outerRadius * 0.4f

        for (i in 0 until maxStars) {
            val cx = outerRadius + i * (starSizePx + spacingPx)
            val cy = outerRadius
            val starFill = (animatedRating - i).coerceIn(0f, 1f)
            drawStar(Offset(cx, cy), outerRadius, innerRadius, starFill, filledColor, emptyColor)
        }
    }
}

private fun computeRating(x: Float, totalWidth: Float, maxStars: Int, allowHalf: Boolean): Float {
    val raw = (x / totalWidth * maxStars).coerceIn(0f, maxStars.toFloat())
    return if (allowHalf) {
        (Math.round(raw * 2) / 2f).coerceIn(0f, maxStars.toFloat())
    } else {
        Math.round(raw).toFloat().coerceIn(0f, maxStars.toFloat())
    }
}

@Composable
fun StarRatingDemo() {
    var rating1 by remember { mutableFloatStateOf(3.5f) }
    var rating2 by remember { mutableFloatStateOf(2f) }
    var rating3 by remember { mutableFloatStateOf(4f) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionTitle("Half-Star Rating (drag or tap)")
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StarRatingBar(rating = rating1, onRatingChange = { rating1 = it })
                Text("$rating1", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        item {
            SectionTitle("Whole-Star Only")
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StarRatingBar(rating = rating2, onRatingChange = { rating2 = it }, allowHalf = false)
                Text("${rating2.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        item {
            SectionTitle("Custom Colors & Sizes")
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StarRatingBar(rating = rating3, onRatingChange = { rating3 = it }, starSize = 28.dp, filledColor = Color(0xFFFF5722), emptyColor = Color(0xFFFFCCBC))
                StarRatingBar(rating = rating3, onRatingChange = { rating3 = it }, starSize = 52.dp, filledColor = Color(0xFF9C27B0), emptyColor = Color(0xFFE1BEE7))
            }
        }

        item {
            SectionTitle("Disabled (Read-Only)")
            StarRatingBar(rating = 4.5f, onRatingChange = {}, enabled = false, filledColor = Color(0xFF607D8B))
        }

        item {
            SectionTitle("10-Star Scale")
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                var r by remember { mutableFloatStateOf(7f) }
                StarRatingBar(rating = r, onRatingChange = { r = it }, maxStars = 10, starSize = 24.dp, spacing = 2.dp)
                Text("$r / 10", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
