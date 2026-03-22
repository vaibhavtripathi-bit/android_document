package com.example.customviews.ui.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs

data class GraphNode(
    val id: String,
    val label: String,
    var position: Offset,
    val color: Color,
    val width: Float = 120f,
    val height: Float = 50f
)

data class NodeConnection(
    val fromId: String,
    val toId: String,
    val color: Color = Color(0xFF666666)
)

@Composable
fun NodeGraphEditor(
    nodes: List<GraphNode>,
    connections: List<NodeConnection>,
    onNodeMoved: (String, Offset) -> Unit,
    onNodeTapped: (String) -> Unit,
    onCanvasTapped: (Offset) -> Unit,
    modifier: Modifier = Modifier,
    selectedNodeId: String? = null,
    connectingFromId: String? = null,
    pendingEndpoint: Offset? = null
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .pointerInput(nodes) {
                detectTapGestures { offset ->
                    val tapped = nodes.find { node ->
                        offset.x in node.position.x..(node.position.x + node.width) &&
                            offset.y in node.position.y..(node.position.y + node.height)
                    }
                    if (tapped != null) onNodeTapped(tapped.id) else onCanvasTapped(offset)
                }
            }
            .pointerInput(nodes) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val dragged = nodes.find { node ->
                        change.position.x - dragAmount.x in node.position.x..(node.position.x + node.width) &&
                            change.position.y - dragAmount.y in node.position.y..(node.position.y + node.height)
                    }
                    dragged?.let {
                        onNodeMoved(it.id, Offset(it.position.x + dragAmount.x, it.position.y + dragAmount.y))
                    }
                }
            }
    ) {
        drawRect(Color(0xFFE8E8E8), size = size)
        val gridSpacing = 30f
        for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
            drawLine(Color(0xFFD8D8D8), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 0.5f)
        }
        for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
            drawLine(Color(0xFFD8D8D8), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 0.5f)
        }

        connections.forEach { conn ->
            val from = nodes.find { it.id == conn.fromId }
            val to = nodes.find { it.id == conn.toId }
            if (from != null && to != null) {
                val start = Offset(from.position.x + from.width, from.position.y + from.height / 2)
                val end = Offset(to.position.x, to.position.y + to.height / 2)
                val cp = abs(end.x - start.x) * 0.5f
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    cubicTo(start.x + cp, start.y, end.x - cp, end.y, end.x, end.y)
                }
                drawPath(path, conn.color, style = Stroke(width = 3f, cap = StrokeCap.Round))
                drawCircle(conn.color, radius = 5f, center = start)
                drawCircle(conn.color, radius = 5f, center = end)
            }
        }

        if (connectingFromId != null && pendingEndpoint != null) {
            val from = nodes.find { it.id == connectingFromId }
            if (from != null) {
                val start = Offset(from.position.x + from.width, from.position.y + from.height / 2)
                val cp = abs(pendingEndpoint.x - start.x) * 0.5f
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    cubicTo(start.x + cp, start.y, pendingEndpoint.x - cp, pendingEndpoint.y, pendingEndpoint.x, pendingEndpoint.y)
                }
                drawPath(path, Color(0xFF2196F3).copy(alpha = 0.6f), style = Stroke(width = 2f, cap = StrokeCap.Round))
            }
        }

        nodes.forEach { node ->
            val isSelected = node.id == selectedNodeId
            val borderColor = if (isSelected) Color(0xFF2196F3) else Color(0xFF999999)
            val borderWidth = if (isSelected) 3f else 1.5f

            drawRoundRect(
                color = Color.White,
                topLeft = node.position,
                size = Size(node.width, node.height),
                cornerRadius = CornerRadius(8f)
            )
            drawRoundRect(
                color = node.color.copy(alpha = 0.15f),
                topLeft = node.position,
                size = Size(node.width, node.height),
                cornerRadius = CornerRadius(8f)
            )
            drawRoundRect(
                color = borderColor,
                topLeft = node.position,
                size = Size(node.width, node.height),
                cornerRadius = CornerRadius(8f),
                style = Stroke(width = borderWidth)
            )

            val dotY = node.position.y + node.height / 2
            drawCircle(Color(0xFF4CAF50), radius = 5f, center = Offset(node.position.x, dotY))
            drawCircle(Color(0xFFF44336), radius = 5f, center = Offset(node.position.x + node.width, dotY))

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                    color = android.graphics.Color.DKGRAY
                    isAntiAlias = true
                    isFakeBoldText = true
                }
                drawText(
                    node.label,
                    node.position.x + node.width / 2,
                    node.position.y + node.height / 2 + 10f,
                    paint
                )
            }
        }
    }
}

@Composable
fun NodeGraphDemo() {
    val nodes = remember {
        mutableStateListOf(
            GraphNode("input", "Input", Offset(30f, 80f), Color(0xFF4CAF50)),
            GraphNode("process", "Process", Offset(220f, 50f), Color(0xFF2196F3)),
            GraphNode("filter", "Filter", Offset(220f, 160f), Color(0xFFFF9800)),
            GraphNode("merge", "Merge", Offset(420f, 100f), Color(0xFF9C27B0)),
            GraphNode("output", "Output", Offset(600f, 100f), Color(0xFFF44336))
        )
    }

    val connections = remember {
        mutableStateListOf(
            NodeConnection("input", "process", Color(0xFF4CAF50)),
            NodeConnection("input", "filter", Color(0xFF4CAF50)),
            NodeConnection("process", "merge", Color(0xFF2196F3)),
            NodeConnection("filter", "merge", Color(0xFFFF9800)),
            NodeConnection("merge", "output", Color(0xFF9C27B0))
        )
    }

    var selectedNode by remember { mutableStateOf<String?>(null) }
    var nodeCount by remember { mutableIntStateOf(nodes.size) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionTitle("Node Graph Editor")
            Text(
                "Drag nodes to reposition. Tap to select. Bezier curves connect output (red) to input (green) ports.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            NodeGraphEditor(
                nodes = nodes,
                connections = connections,
                selectedNodeId = selectedNode,
                onNodeMoved = { id, newPos ->
                    val index = nodes.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        nodes[index] = nodes[index].copy(position = newPos)
                    }
                },
                onNodeTapped = { id ->
                    selectedNode = if (selectedNode == id) null else id
                },
                onCanvasTapped = { selectedNode = null },
                modifier = Modifier.fillMaxWidth().height(350.dp)
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    nodeCount++
                    nodes.add(
                        GraphNode(
                            id = "node_$nodeCount",
                            label = "Node $nodeCount",
                            position = Offset(100f + (nodeCount % 4) * 80f, 200f + (nodeCount % 3) * 60f),
                            color = listOf(Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFFF44336))[nodeCount % 5]
                        )
                    )
                }) { Text("Add Node") }

                if (selectedNode != null) {
                    Button(
                        onClick = {
                            connections.removeAll { it.fromId == selectedNode || it.toId == selectedNode }
                            nodes.removeAll { it.id == selectedNode }
                            selectedNode = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) { Text("Delete Selected") }
                }

                OutlinedButton(onClick = {
                    if (nodes.size >= 2) {
                        val from = nodes[nodes.size - 2]
                        val to = nodes.last()
                        if (connections.none { it.fromId == from.id && it.toId == to.id }) {
                            connections.add(NodeConnection(from.id, to.id))
                        }
                    }
                }) { Text("Connect Last Two") }
            }
        }

        item {
            selectedNode?.let { id ->
                val node = nodes.find { it.id == id }
                if (node != null) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Selected: ${node.label}", fontWeight = FontWeight.Bold)
                            Text("ID: ${node.id}", style = MaterialTheme.typography.bodySmall)
                            Text("Position: (${node.position.x.toInt()}, ${node.position.y.toInt()})", style = MaterialTheme.typography.bodySmall)
                            val inbound = connections.count { it.toId == id }
                            val outbound = connections.count { it.fromId == id }
                            Text("Connections: $inbound in, $outbound out", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
