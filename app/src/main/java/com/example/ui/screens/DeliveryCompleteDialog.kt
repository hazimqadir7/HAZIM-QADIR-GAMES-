package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.CargoJob
import com.example.data.models.GameData
import com.example.data.models.TelemetryState
import kotlin.math.max
import kotlin.math.roundToLong

@Composable
fun DeliveryCompleteDialog(
    job: CargoJob,
    telemetry: TelemetryState,
    currentLevel: Int,
    currentXp: Long,
    onClaimAndContinue: (Long, Long) -> Unit
) {
    val damageDeduction = (job.baseRewardInr * (telemetry.cargoDamagePct / 100f) * 0.4f).roundToLong()
    val olengBonus = (telemetry.olengScore * 10).coerceAtMost(50000L)
    val totalEarned = max(15000L, job.baseRewardInr - damageDeduction + olengBonus)

    val baseXp = 250L
    val olengBonusXp = (telemetry.olengScore / 15L).coerceAtMost(200L)
    val safeDrivingBonusXp = if (telemetry.cargoDamagePct <= 0f) 150L else if (telemetry.cargoDamagePct < 15f) 75L else 0L
    val totalXpEarned = baseXp + olengBonusXp + safeDrivingBonusXp

    val nextLevelThresholds = listOf(0L, 300L, 750L, 1400L, 2300L, 3500L, 5000L, 7000L, 9500L, 12500L)
    val nextXpTarget = if (currentLevel < nextLevelThresholds.size) nextLevelThresholds[currentLevel] else 15000L
    val title = GameData.DRIVER_TITLES.getOrElse(currentLevel - 1) { "Himalayan Legend" }

    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .width(420.dp)
                .background(Color(0xFF09090B), RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Success Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape)
                        .border(2.dp, Color(0xFF10B981), CircleShape)
                        .padding(10.dp)
                ) {
                    Text(text = "✓", color = Color(0xFF10B981), fontSize = 28.sp, fontWeight = FontWeight.Black)
                }

                Text(
                    text = "HIMALAYAN HAUL COMPLETE!",
                    color = Color(0xFFFACC15),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "${job.cargoName} delivered to ${job.destinationCity}",
                    color = Color(0xFFE2E8F0),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                // Financial Breakdown
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF18181B), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF27272A), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Freight Contract Base:", color = Color(0xFFA1A1AA), fontSize = 11.sp)
                            Text(text = "₹${job.baseRewardInr}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        if (olengBonus > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Mountain Sway Bonus:", color = Color(0xFFF59E0B), fontSize = 11.sp)
                                Text(text = "+₹$olengBonus", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        if (damageDeduction > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Cargo Damage Penalty (${telemetry.cargoDamagePct.toInt()}%):", color = Color(0xFFEF4444), fontSize = 11.sp)
                                Text(text = "-₹$damageDeduction", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "TOTAL EARNED:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            Text(
                                text = "₹$totalEarned",
                                color = Color(0xFF10B981),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Driver XP & Rank
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF18181B), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Level $currentLevel: $title", color = Color(0xFFFDE047), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "+$totalXpEarned XP", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }

                        // Mini progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(Color(0xFF27272A), RoundedCornerShape(3.dp))
                        ) {
                            val xpProgress = ((currentXp + totalXpEarned).toFloat() / nextXpTarget.toFloat()).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(xpProgress)
                                    .height(6.dp)
                                    .background(
                                        Brush.horizontalGradient(listOf(Color(0xFFD97706), Color(0xFFFACC15))),
                                        RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }
                }

                // Claim Button
                Button(
                    onClick = { onClaimAndContinue(totalEarned, totalXpEarned) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "CLAIM ₹$totalEarned & RETURN TO GARAGE",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
