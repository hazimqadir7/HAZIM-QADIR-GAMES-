package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CargoJob
import com.example.data.models.GameData
import com.example.data.models.RouteLocation
import com.example.data.models.TelemetryState
import kotlin.math.abs
import kotlin.math.max

@Composable
fun MiniMapRadar(
    telemetry: TelemetryState,
    route: RouteLocation,
    activeJob: CargoJob?,
    modifier: Modifier = Modifier
) {
    val currentDist = telemetry.distanceTraveledMeters
    val turns = GameData.ROUTE_TURNS[route.id] ?: emptyList()
    val upcomingTurn = turns.find { it.distanceMeters > currentDist && (it.distanceMeters - currentDist) <= 300f }
    val distToTurn = upcomingTurn?.let { max(0, (it.distanceMeters - currentDist).toInt()) }
    val stations = GameData.ROUTE_FUEL_STATIONS[route.id] ?: emptyList()
    val upcomingStation = stations.find { it.distanceMeters > currentDist && (it.distanceMeters - currentDist) <= 300f }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Upcoming Turn Alert Banner
        if (upcomingTurn != null && distToTurn != null) {
            val isSevere = upcomingTurn.direction.contains("hairpin") || upcomingTurn.direction.contains("sharp")
            Box(
                modifier = Modifier
                    .background(
                        if (isSevere) Color(0xD97F1D1D) else Color(0xD978350F),
                        RoundedCornerShape(10.dp)
                    )
                    .border(1.dp, if (isSevere) Color(0xFFEF4444) else Color(0xFFF59E0B), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (upcomingTurn.direction.contains("left")) "↰" else "↱",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = upcomingTurn.name,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${distToTurn}m",
                            color = Color(0xFFFDE047),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = "Limit: ${upcomingTurn.advisorySpeedKmh} KM/H  •  Grade: +${upcomingTurn.inclineGradePct}%",
                        color = Color(0xFFE2E8F0),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Radar Circular Screen
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(Color(0xE609090B), RoundedCornerShape(20.dp))
                .border(1.5.dp, Color(0xFF3F3F46), RoundedCornerShape(20.dp))
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(98.dp)) {
                val cx = size.width / 2f
                val cy = size.height * 0.72f

                // Radar rings
                drawCircle(color = Color(0x1A10B981), radius = size.width * 0.45f, center = Offset(cx, cy))
                drawCircle(color = Color(0x1A10B981), radius = size.width * 0.25f, center = Offset(cx, cy))
                drawLine(color = Color(0x1A10B981), start = Offset(0f, cy), end = Offset(size.width, cy))
                drawLine(color = Color(0x1A10B981), start = Offset(cx, 0f), end = Offset(cx, size.height))

                // Road line path projected ahead
                val roadPath = Path()
                val leftPath = Path()
                val rightPath = Path()

                roadPath.moveTo(cx, cy)
                leftPath.moveTo(cx - 10.dp.toPx(), cy)
                rightPath.moveTo(cx + 10.dp.toPx(), cy)

                var currentX = cx
                for (step in 1..10) {
                    val sampleDist = currentDist + step * 25f
                    val sampleY = cy - (step * 7.dp.toPx())

                    var curvePush = 0f
                    turns.forEach { t ->
                        val d = sampleDist - t.distanceMeters
                        if (abs(d) < 80f) {
                            val factor = (1f - abs(d) / 80f)
                            curvePush += (t.angleDegrees / 90f) * 16.dp.toPx() * factor
                        }
                    }

                    currentX = cx + curvePush
                    roadPath.lineTo(currentX, sampleY)
                    leftPath.lineTo(currentX - 8.dp.toPx(), sampleY)
                    rightPath.lineTo(currentX + 8.dp.toPx(), sampleY)
                }

                // Draw asphalt road edges
                drawPath(leftPath, Color(0xFF71717A), style = Stroke(width = 1.2.dp.toPx()))
                drawPath(rightPath, Color(0xFF71717A), style = Stroke(width = 1.2.dp.toPx()))
                // Yellow center divider dashed line
                drawPath(roadPath, Color(0xFFFACC15), style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round))

                // Player truck chevron
                drawCircle(color = Color(0xFF10B981), radius = 3.dp.toPx(), center = Offset(cx, cy))
                val chevronPath = Path().apply {
                    moveTo(cx, cy - 5.dp.toPx())
                    lineTo(cx - 4.dp.toPx(), cy + 4.dp.toPx())
                    lineTo(cx, cy + 2.dp.toPx())
                    lineTo(cx + 4.dp.toPx(), cy + 4.dp.toPx())
                    close()
                }
                drawPath(chevronPath, Color(0xFF34D399))

                // Upcoming Indian Oil petrol station blip
                if (upcomingStation != null) {
                    val relDist = upcomingStation.distanceMeters - currentDist
                    if (relDist in 0f..250f) {
                        val sy = cy - (relDist / 250f * 65.dp.toPx())
                        val sx = if (upcomingStation.side == "left") cx - 14.dp.toPx() else cx + 14.dp.toPx()
                        drawCircle(color = Color(0xFFEA580C), radius = 3.5.dp.toPx(), center = Offset(sx, sy))
                    }
                }
            }

            // Route Progress footer
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val remainingKm = max(0f, (telemetry.totalRouteMeters - currentDist) / 1000f)
                Text(
                    text = "${String.format("%.1f", remainingKm)} KM REMAINING",
                    color = Color(0xFFA1A1AA),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
