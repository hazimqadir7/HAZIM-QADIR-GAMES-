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
fun AnalogFuelGauge(
    fuelPct: Float,
    isRefueling: Boolean = false,
    dashboardColor: DashboardLightColor = DashboardLightColor.AMBER,
    modifier: Modifier = Modifier.size(86.dp)
) {
    val themeColor = Color(dashboardColor.primaryColorHex)
    val clampedPct = fuelPct.coerceIn(0f, 100f)
    val minAngle = -100f
    val maxAngle = 100f
    val sweep = maxAngle - minAngle
    val needleAngle = minAngle + (clampedPct / 100f) * sweep
    val isLowFuel = clampedPct <= 20f

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

            // Outer gauge arc
            drawArc(
                color = Color(0xFF27272A),
                startAngle = 170f,
                sweepAngle = 200f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Low fuel red reserve arc (0 to 20%)
            drawArc(
                color = Color(0xFFEF4444).copy(alpha = 0.9f),
                startAngle = 170f,
                sweepAngle = 40f,
                useCenter = false,
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Full fuel green arc (80 to 100%)
            drawArc(
                color = Color(0xFF10B981).copy(alpha = 0.8f),
                startAngle = 330f,
                sweepAngle = 40f,
                useCenter = false,
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Ticks (0, 25, 50, 75, 100)
            for (p in 0..100 step 25) {
                val fraction = p / 100f
                val angleDeg = minAngle + fraction * sweep
                val rad = ((angleDeg - 90f) * PI / 180f).toFloat()
                val innerR = radius - 8.dp.toPx()
                val outerR = radius - 2.dp.toPx()

                val tickColor = if (p <= 20) Color(0xFFEF4444) else themeColor.copy(alpha = 0.8f)
                drawLine(
                    color = tickColor,
                    start = Offset(cx + cos(rad) * innerR, cy + sin(rad) * innerR),
                    end = Offset(cx + cos(rad) * outerR, cy + sin(rad) * outerR),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Needle
            val needleRad = ((needleAngle - 90f) * PI / 180f).toFloat()
            val needleLen = radius - 4.dp.toPx()
            drawLine(
                color = if (isLowFuel) Color(0xFFEF4444) else Color(0xFFF59E0B),
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
                text = "${clampedPct.toInt()}%",
                color = if (isLowFuel) Color(0xFFEF4444) else if (isRefueling) Color(0xFF10B981) else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = if (isRefueling) "PUMPING" else "DIESEL",
                color = if (isLowFuel) Color(0xFFEF4444) else Color(0xFFA1A1AA),
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
