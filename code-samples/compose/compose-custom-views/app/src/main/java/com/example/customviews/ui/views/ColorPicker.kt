package com.example.customviews.ui.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private fun hsvToColor(hue: Float, saturation: Float, value: Float): Color {
    val hsv = floatArrayOf(hue, saturation, value)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

private fun colorToHex(color: Color): String {
    val argb = color.toArgb()
    return String.format("#%06X", 0xFFFFFF and argb)
}

@Composable
fun ColorPickerWheel(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    wheelSize: Float = 300f
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var brightness by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(hue, saturation, brightness) {
        onColorSelected(hsvToColor(hue, saturation, brightness))
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier
                    .size(wheelSize.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            val dist = sqrt(dx * dx + dy * dy)
                            val outerR = size.width / 2f
                            val innerR = outerR * 0.65f
                            if (dist in innerR..outerR) {
                                hue = ((Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360) % 360).toFloat()
                            } else if (dist < innerR) {
                                saturation = (dist / innerR).coerceIn(0f, 1f)
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = change.position.x - center.x
                            val dy = change.position.y - center.y
                            val dist = sqrt(dx * dx + dy * dy)
                            val outerR = size.width / 2f
                            val innerR = outerR * 0.65f
                            if (dist >= innerR) {
                                hue = ((Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360) % 360).toFloat()
                            } else {
                                saturation = (dist / innerR).coerceIn(0f, 1f)
                            }
                        }
                    }
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerR = size.width / 2f
                val innerR = outerR * 0.65f
                val ringWidth = outerR - innerR

                val hueColors = (0..360 step 1).map { hsvToColor(it.toFloat(), 1f, 1f) }
                drawCircle(
                    brush = Brush.sweepGradient(hueColors, center),
                    radius = outerR,
                    center = center,
                    style = Stroke(width = ringWidth)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, hsvToColor(hue, 1f, brightness)),
                        center = center,
                        radius = innerR
                    ),
                    radius = innerR,
                    center = center
                )

                val hueAngle = Math.toRadians(hue.toDouble())
                val indicatorR = innerR + ringWidth / 2
                val ix = center.x + (indicatorR * cos(hueAngle)).toFloat()
                val iy = center.y + (indicatorR * sin(hueAngle)).toFloat()
                drawCircle(Color.White, radius = ringWidth / 2.5f, center = Offset(ix, iy), style = Stroke(3f))
                drawCircle(hsvToColor(hue, 1f, 1f), radius = ringWidth / 3f, center = Offset(ix, iy))

                val satX = center.x + (saturation * innerR * cos(hueAngle)).toFloat()
                val satY = center.y + (saturation * innerR * sin(hueAngle)).toFloat()
                drawCircle(Color.White, radius = 10f, center = Offset(satX, satY), style = Stroke(2f))
                drawCircle(selectedColor, radius = 7f, center = Offset(satX, satY))
            }
        }

        Text("Brightness", style = MaterialTheme.typography.labelMedium)
        Slider(
            value = brightness,
            onValueChange = { brightness = it },
            modifier = Modifier.width(wheelSize.dp),
            colors = SliderDefaults.colors(
                thumbColor = selectedColor,
                activeTrackColor = selectedColor
            )
        )
    }
}

@Composable
fun ColorPickerDemo() {
    var selectedColor by remember { mutableStateOf(Color.Red) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionTitle("Color Picker Wheel")
            Text(
                "Drag on the ring for hue, inside for saturation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            ColorPickerWheel(selectedColor = selectedColor, onColorSelected = { selectedColor = it })
        }

        item {
            SectionTitle("Selected Color")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(selectedColor)
                        .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                )
                Column {
                    Text("HEX: ${colorToHex(selectedColor)}", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(
                        "RGB: ${(selectedColor.red * 255).toInt()}, ${(selectedColor.green * 255).toInt()}, ${(selectedColor.blue * 255).toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        item {
            SectionTitle("Preview")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = selectedColor.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sample Card", fontWeight = FontWeight.Bold, color = selectedColor)
                    Text("This card uses the selected color for styling.", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = selectedColor)
                    ) {
                        Text("Action Button")
                    }
                }
            }
        }
    }
}
