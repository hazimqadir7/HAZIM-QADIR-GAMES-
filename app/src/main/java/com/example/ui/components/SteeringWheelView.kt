package com.example.ui.components

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.atan2

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SteeringWheelView(
    onSteer: (Float) -> Unit,
    onHorn: () -> Unit,
    sizeDp: Dp = 125.dp,
    modifier: Modifier = Modifier
) {
    var rawAngle by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var centerOffset by remember { mutableStateOf(Offset.Zero) }

    val animatedAngle by animateFloatAsState(
        targetValue = if (isDragging) rawAngle else 0f,
        animationSpec = tween(durationMillis = if (isDragging) 16 else 140),
        label = "wheelRotation"
    )

    val maxAngle = 90f

    Box(
        modifier = modifier
            .size(sizeDp)
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isDragging = true
                        val dx = event.x - (centerOffset.x)
                        val dy = event.y - (centerOffset.y)
                        val angle = (atan2(dy, dx) * 180f / PI.toFloat()).coerceIn(-maxAngle, maxAngle)
                        rawAngle = angle
                        onSteer(angle / maxAngle)
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.x - (centerOffset.x)
                        val dy = event.y - (centerOffset.y)
                        val angle = (atan2(dy, dx) * 180f / PI.toFloat()).coerceIn(-maxAngle, maxAngle)
                        rawAngle = angle
                        onSteer(angle / maxAngle)
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isDragging = false
                        rawAngle = 0f
                        onSteer(0f)
                        true
                    }
                    else -> false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Rotating Wheel Graphics
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(animatedAngle),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                centerOffset = Offset(size.width / 2f, size.height / 2f)
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = size.width * 0.45f

                // Outer Steering Rim
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF27272A), Color(0xFF18181B), Color(0xFF09090B)),
                        center = Offset(cx, cy),
                        radius = r
                    ),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )

                // Top Yellow Center Strip Marker (12 O'Clock)
                drawArc(
                    color = Color(0xFFF59E0B),
                    startAngle = 265f,
                    sweepAngle = 10f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx())
                )

                // Heavy 3-spoke structure (Left, Right, Bottom)
                val spokeColor = Color(0xFF3F3F46)
                // Left spoke
                drawLine(
                    color = spokeColor,
                    start = Offset(cx - r + 8.dp.toPx(), cy),
                    end = Offset(cx - 14.dp.toPx(), cy),
                    strokeWidth = 9.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Right spoke
                drawLine(
                    color = spokeColor,
                    start = Offset(cx + 14.dp.toPx(), cy),
                    end = Offset(cx + r - 8.dp.toPx(), cy),
                    strokeWidth = 9.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Bottom vertical spoke
                drawLine(
                    color = spokeColor,
                    start = Offset(cx, cy + 14.dp.toPx()),
                    end = Offset(cx, cy + r - 8.dp.toPx()),
                    strokeWidth = 9.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Center Horn Pad
            Box(
                modifier = Modifier
                    .size(sizeDp * 0.36f)
                    .background(Color(0xFF18181B), CircleShape)
                    .border(2.dp, Color(0xFFF59E0B), CircleShape)
                    .clickable { onHorn() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HORN",
                    color = Color(0xFFF59E0B),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
