package com.example.customviews.ui.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitPointerEventScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.awaitPointerEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PathData(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun SignaturePad(
    modifier: Modifier = Modifier,
    penColor: Color = Color.Black,
    penWidth: Dp = 3.dp,
    backgroundColor: Color = Color.White,
    paths: List<PathData>,
    currentPath: Path?,
    currentColor: Color,
    currentStrokeWidth: Float,
    onDrawStart: (Offset) -> Unit,
    onDrawMove: (Offset) -> Unit,
    onDrawEnd: () -> Unit
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val position = event.changes.first().position
                        when (event.type) {
                            PointerEventType.Press -> {
                                event.changes.forEach { it.consume() }
                                onDrawStart(position)
                            }
                            PointerEventType.Move -> {
                                event.changes.forEach { it.consume() }
                                onDrawMove(position)
                            }
                            PointerEventType.Release -> {
                                event.changes.forEach { it.consume() }
                                onDrawEnd()
                            }
                        }
                    }
                }
            }
    ) {
        paths.forEach { pathData ->
            drawPath(
                path = pathData.path,
                color = pathData.color,
                style = Stroke(
                    width = pathData.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        currentPath?.let {
            drawPath(
                path = it,
                color = currentColor,
                style = Stroke(
                    width = currentStrokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Composable
fun SignaturePadDemo() {
    val paths = remember { mutableStateListOf<PathData>() }
    val redoPaths = remember { mutableStateListOf<PathData>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var strokeWidth by remember { mutableFloatStateOf(4f) }

    val colors = listOf(Color.Black, Color(0xFF2196F3), Color(0xFFF44336), Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF9C27B0))

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionTitle("Signature Pad")
            Text("Draw with your finger or mouse", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            SignaturePad(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                paths = paths,
                currentPath = currentPath,
                currentColor = selectedColor,
                currentStrokeWidth = strokeWidth,
                onDrawStart = { offset ->
                    redoPaths.clear()
                    currentPath = Path().apply { moveTo(offset.x, offset.y) }
                },
                onDrawMove = { offset ->
                    currentPath = currentPath?.let { old ->
                        Path().apply {
                            addPath(old)
                            lineTo(offset.x, offset.y)
                        }
                    }
                },
                onDrawEnd = {
                    currentPath?.let { path ->
                        paths.add(PathData(path, selectedColor, strokeWidth))
                    }
                    currentPath = null
                }
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (paths.isNotEmpty()) {
                            redoPaths.add(paths.removeLast())
                        }
                    },
                    enabled = paths.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, "Undo")
                }
                IconButton(
                    onClick = {
                        if (redoPaths.isNotEmpty()) {
                            paths.add(redoPaths.removeLast())
                        }
                    },
                    enabled = redoPaths.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Redo, "Redo")
                }
                IconButton(
                    onClick = {
                        paths.clear()
                        redoPaths.clear()
                    },
                    enabled = paths.isNotEmpty()
                ) {
                    Icon(Icons.Default.Delete, "Clear")
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("${paths.size} strokes", style = MaterialTheme.typography.labelSmall)
            }
        }

        item {
            SectionTitle("Pen Color")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { color ->
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (color == selectedColor) 3.dp else 1.dp,
                                color = if (color == selectedColor) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedColor = color },
                        color = color
                    ) {}
                }
            }
        }

        item {
            SectionTitle("Stroke Width: ${strokeWidth.toInt()}px")
            Slider(
                value = strokeWidth,
                onValueChange = { strokeWidth = it },
                valueRange = 1f..20f,
                steps = 18,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(2f, 4f, 8f, 12f).forEach { w ->
                    FilterChip(
                        selected = strokeWidth == w,
                        onClick = { strokeWidth = w },
                        label = { Text("${w.toInt()}px") }
                    )
                }
            }
        }
    }
}
