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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DashboardLightColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogTachometer(
    rpm: Float,
    maxRpm: Float = 3500f,
    redlineRpm: Float = 2600f,
    dashboardColor: DashboardLightColor = DashboardLightColor.AMBER,
    modifier: Modifier = Modifier.size(86.dp)
) {
    val themeColor = Color(dashboardColor.primaryColorHex)
    val clampedRpm = rpm.coerceIn(0f, maxRpm)
    val minAngle = -125f
    val maxAngle = 125f
    val sweep = maxAngle - minAngle
    val needleAngle = minAngle + (clampedRpm / maxRpm) * sweep

    Box(
        modifier = modifier
            .background(Color(0xFF09090B), CircleShape)
            .border(2.dp, themeColor.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(82.dp)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = size.width * 0.44f

            // Eco zone arc (1200 - 2000 RPM)
            val ecoStartFraction = 1200f / maxRpm
            val ecoEndFraction = 2000f / maxRpm
            val ecoStart = 145f + (ecoStartFraction * 250f)
            val ecoSweep = (ecoEndFraction - ecoStartFraction) * 250f
            drawArc(
                color = Color(0xFF10B981).copy(alpha = 0.8f),
                startAngle = ecoStart,
                sweepAngle = ecoSweep,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Redline arc (2600 - 3500 RPM)
            val redStartFraction = redlineRpm / maxRpm
            val redStart = 145f + (redStartFraction * 250f)
            val redSweep = (1f - redStartFraction) * 250f
            drawArc(
                color = Color(0xFFEF4444).copy(alpha = 0.85f),
                startAngle = redStart,
                sweepAngle = redSweep,
                useCenter = false,
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Ticks
            for (r in 0..maxRpm.toInt() step 250) {
                val fraction = r / maxRpm
                val angleDeg = minAngle + fraction * sweep
                val rad = ((angleDeg - 90f) * PI / 180f).toFloat()
                val isMajor = r % 500 == 0
                val innerR = radius - (if (isMajor) 8.dp.toPx() else 5.dp.toPx())
                val outerR = radius - 2.dp.toPx()

                val tickColor = if (r >= redlineRpm) Color(0xFFEF4444) else if (r in 1200..2000) Color(0xFF10B981) else themeColor.copy(alpha = 0.7f)
                drawLine(
                    color = tickColor,
                    start = Offset(cx + cos(rad) * innerR, cy + sin(rad) * innerR),
                    end = Offset(cx + cos(rad) * outerR, cy + sin(rad) * outerR),
                    strokeWidth = if (isMajor) 1.8.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Needle
            val needleRad = ((needleAngle - 90f) * PI / 180f).toFloat()
            val needleLen = radius - 4.dp.toPx()
            drawLine(
                color = Color(0xFFEF4444),
                start = Offset(cx, cy),
                end = Offset(cx + cos(needleRad) * needleLen, cy + sin(needleRad) * needleLen),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Pivot
            drawCircle(color = Color(0xFF18181B), radius = 5.dp.toPx(), center = Offset(cx, cy))
            drawCircle(color = themeColor, radius = 2.5.dp.toPx(), center = Offset(cx, cy))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = "${(clampedRpm / 100).toInt()}",
                color = if (rpm >= redlineRpm) Color(0xFFEF4444) else Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "RPM x100",
                color = Color(0xFFA1A1AA),
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
