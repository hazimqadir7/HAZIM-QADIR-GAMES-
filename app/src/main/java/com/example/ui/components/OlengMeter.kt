package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun OlengMeter(
    bodyRollAngle: Float,
    olengCombo: Int,
    olengScore: Long,
    olengRatingText: String,
    modifier: Modifier = Modifier
) {
    val clampedAngle = bodyRollAngle.coerceIn(-22f, 22f)
    val absAngle = abs(clampedAngle)
    val isTiltingLeft = clampedAngle > 0.4f
    val isTiltingRight = clampedAngle < -0.4f

    val tierColor = when {
        absAngle >= 16f -> Color(0xFFEF4444)
        absAngle >= 11f -> Color(0xFFF97316)
        absAngle >= 6f -> Color(0xFFF59E0B)
        absAngle >= 2.5f -> Color(0xFFFACC15)
        else -> Color(0xFF10B981)
    }

    Box(
        modifier = modifier
            .background(Color(0xE609090B), RoundedCornerShape(16.dp))
            .border(1.5.dp, if (olengCombo > 0) Color(0xFFF59E0B) else Color(0xFF27272A), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Top Row: Title, Combo Badge, Points
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "PAHADI SWAY",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )

                if (olengCombo > 0) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFDC2626), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${olengCombo}x COMBO",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Text(
                    text = "+$olengScore PTS",
                    color = Color(0xFFFDE047),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "${String.format("%.1f", absAngle)}°",
                    color = tierColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Bi-Directional Sway Bar (-20 deg to +20 deg)
            Row(
                modifier = Modifier
                    .width(180.dp)
                    .height(8.dp)
                    .background(Color(0xFF18181B), RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(4.dp))
                    .padding(1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left sway bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (isTiltingLeft) {
                        val leftFrac = (absAngle / 20f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(leftFrac)
                                .background(tierColor, RoundedCornerShape(2.dp))
                        )
                    }
                }

                // Center divider
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(10.dp)
                        .background(Color.White, CircleShape)
                )

                // Right sway bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isTiltingRight) {
                        val rightFrac = (absAngle / 20f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(rightFrac)
                                .background(tierColor, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }

            if (olengCombo > 0) {
                Text(
                    text = olengRatingText,
                    color = Color(0xFFFDE047),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
