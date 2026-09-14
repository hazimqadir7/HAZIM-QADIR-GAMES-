package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DashboardLightColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogSpeedometer(
    speedKmh: Float,
    maxSpeed: Float = 140f,
    currentGear: Int = 0,
    tripKm: Float = 0f,
    dashboardColor: DashboardLightColor = DashboardLightColor.AMBER,
    modifier: Modifier = Modifier.size(96.dp)
) {
    val themeColor = Color(dashboardColor.primaryColorHex)
    val clampedSpeed = speedKmh.coerceIn(0f, maxSpeed)
    val minAngle = -125f
    val maxAngle = 125f
    val sweep = maxAngle - minAngle
    val needleAngle = minAngle + (clampedSpeed / maxSpeed) * sweep

    Box(
        modifier = modifier
            .background(Color(0xFF09090B), CircleShape)
            .border(2.dp, themeColor.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(92.dp)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = size.width * 0.44f

            // Background glow halo
            drawCircle(
                color = themeColor.copy(alpha = 0.08f),
                radius = radius,
                center = Offset(cx, cy)
            )

            // Outer gauge arc
            drawArc(
                color = Color(0xFF27272A),
                startAngle = 145f,
                sweepAngle = 250f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Over-speed red arc (80 to 140 km/h)
            val overspeedFraction = 80f / maxSpeed
            val overspeedStart = 145f + (overspeedFraction * 250f)
            val overspeedSweep = (1f - overspeedFraction) * 250f
            drawArc(
                color = Color(0xFFEF4444).copy(alpha = 0.85f),
                startAngle = overspeedStart,
                sweepAngle = overspeedSweep,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Ticks
            val nativePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 9.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.MONOSPACE
            }

            for (s in 0..maxSpeed.toInt() step 10) {
                val fraction = s / maxSpeed
                val angleDeg = minAngle + fraction * sweep
                val rad = ((angleDeg - 90f) * PI / 180f).toFloat()
                val isMajor = s % 20 == 0
                val innerR = radius - (if (isMajor) 10.dp.toPx() else 6.dp.toPx())
                val outerR = radius - 2.dp.toPx()

                val x1 = cx + cos(rad) * innerR
                val y1 = cy + sin(rad) * innerR
                val x2 = cx + cos(rad) * outerR
                val y2 = cy + sin(rad) * outerR

                val tickColor = if (s >= 80) Color(0xFFEF4444) else themeColor.copy(alpha = 0.8f)
                drawLine(
                    color = tickColor,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )

                if (isMajor && s % 40 == 0) {
                    val textR = radius - 18.dp.toPx()
                    val tx = cx + cos(rad) * textR
                    val ty = cy + sin(rad) * textR + 3.dp.toPx()
                    drawContext.canvas.nativeCanvas.drawText(s.toString(), tx, ty, nativePaint)
                }
            }

            // Needle
            val needleRad = ((needleAngle - 90f) * PI / 180f).toFloat()
            val needleLen = radius - 5.dp.toPx()
            val nx = cx + cos(needleRad) * needleLen
            val ny = cy + sin(needleRad) * needleLen

            drawLine(
                color = Color(0xFFEF4444),
                start = Offset(cx, cy),
                end = Offset(nx, ny),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center hub
            drawCircle(color = Color(0xFF18181B), radius = 6.dp.toPx(), center = Offset(cx, cy))
            drawCircle(color = themeColor, radius = 3.dp.toPx(), center = Offset(cx, cy))
        }

        // Digital speed readout inside dial
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "${speedKmh.toInt()}",
                color = if (speedKmh >= 80) Color(0xFFEF4444) else Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "KM/H",
                color = Color(0xFFA1A1AA),
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
