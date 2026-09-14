package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VehicleConditionCard(
    engineHealthPct: Float,
    tireWearPct: Float,
    cargoDamagePct: Float,
    engineTempC: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xD909090B), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TRUCK WEAR",
                    color = Color(0xFFA1A1AA),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                if (engineTempC > 105f) {
                    Text(
                        text = "TEMP: ${engineTempC.toInt()}°C (HIGH)",
                        color = Color(0xFFEF4444),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Engine health bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "ENG",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFF27272A), RoundedCornerShape(2.dp))
                ) {
                    val frac = (engineHealthPct / 100f).coerceIn(0f, 1f)
                    val color = if (engineHealthPct > 60f) Color(0xFF10B981) else if (engineHealthPct > 30f) Color(0xFFF59E0B) else Color(0xFFEF4444)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(frac)
                            .height(4.dp)
                            .background(color, RoundedCornerShape(2.dp))
                    )
                }
                Text(
                    text = "${engineHealthPct.toInt()}%",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Tire wear bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "TIRE",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFF27272A), RoundedCornerShape(2.dp))
                ) {
                    val treadFrac = ((100f - tireWearPct) / 100f).coerceIn(0f, 1f)
                    val color = if (treadFrac > 0.6f) Color(0xFF10B981) else if (treadFrac > 0.3f) Color(0xFFF59E0B) else Color(0xFFEF4444)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(treadFrac)
                            .height(4.dp)
                            .background(color, RoundedCornerShape(2.dp))
                    )
                }
                Text(
                    text = "${(100f - tireWearPct).toInt()}%",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (cargoDamagePct > 0f) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "CARGO DMG",
                        color = Color(0xFFEF4444),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${cargoDamagePct.toInt()}%",
                        color = Color(0xFFEF4444),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
