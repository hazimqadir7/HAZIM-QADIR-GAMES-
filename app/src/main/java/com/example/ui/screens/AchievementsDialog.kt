package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.database.PlayerProfileEntity
import com.example.data.models.AchievementBadge
import com.example.data.models.GameData

@Composable
fun AchievementsDialog(
    profile: PlayerProfileEntity,
    onClaim: (AchievementBadge) -> Unit,
    onDismiss: () -> Unit
) {
    val claimedSet = profile.claimedAchievementsCsv.split(",").filter { it.isNotBlank() }.toSet()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(480.dp)
                .fillMaxHeight(0.85f)
                .background(Color(0xFF09090B), RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🏆 HIMALAYAN MILESTONES & ACHIEVEMENTS",
                            color = Color(0xFFF59E0B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Log highway kilometers, jobs, and mountain sway to claim INR rewards",
                            color = Color(0xFFA1A1AA),
                            fontSize = 9.sp
                        )
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close", color = Color.White, fontSize = 10.sp)
                    }
                }

                // Badges List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(GameData.ACHIEVEMENTS) { badge ->
                        val currentVal = when (badge.category) {
                            "distance" -> profile.totalDistanceMeters
                            "jobs" -> profile.completedJobsCount.toLong()
                            "oleng" -> profile.highOlengScore
                            "garage" -> profile.unlockedTrucksCsv.split(",").size.toLong()
                            "endurance" -> profile.refuelCount.toLong()
                            else -> 0L
                        }

                        val isUnlocked = currentVal >= badge.targetValue
                        val isClaimed = claimedSet.contains(badge.id)
                        val progressFrac = (currentVal.toFloat() / badge.targetValue.toFloat()).coerceIn(0f, 1f)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF18181B), RoundedCornerShape(14.dp))
                                .border(
                                    1.dp,
                                    if (isClaimed) Color(0xFF10B981).copy(alpha = 0.5f) else if (isUnlocked) Color(0xFFF59E0B) else Color(0xFF27272A),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = badge.title,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = badge.rarity.uppercase(),
                                            color = when (badge.rarity) {
                                                "diamond" -> Color(0xFF38BDF8)
                                                "gold" -> Color(0xFFFDE047)
                                                "silver" -> Color(0xFFE2E8F0)
                                                else -> Color(0xFFD97706)
                                            },
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Text(
                                        text = badge.description,
                                        color = Color(0xFFA1A1AA),
                                        fontSize = 9.sp,
                                        lineHeight = 12.sp
                                    )

                                    // Progress bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .height(4.dp)
                                            .background(Color(0xFF27272A), RoundedCornerShape(2.dp))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progressFrac)
                                                .height(4.dp)
                                                .background(if (isUnlocked) Color(0xFF10B981) else Color(0xFFF59E0B), RoundedCornerShape(2.dp))
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "+₹${badge.rewardMoneyInr}",
                                        color = Color(0xFFFACC15),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    if (isClaimed) {
                                        Text(
                                            text = "CLAIMED ✓",
                                            color = Color(0xFF10B981),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    } else if (isUnlocked) {
                                        Button(
                                            onClick = { onClaim(badge) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("CLAIM", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }
                                    } else {
                                        Text(
                                            text = "${(progressFrac * 100).toInt()}%",
                                            color = Color(0xFF71717A),
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
