package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.RadioStation

@Composable
fun RadioPlayerView(
    station: RadioStation,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onNextStation: () -> Unit,
    onPrevStation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "eqAnim")
    val h1 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(210), RepeatMode.Reverse), label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(170), RepeatMode.Reverse), label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 0.15f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(260), RepeatMode.Reverse), label = "h3"
    )
    val h4 by transition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(190), RepeatMode.Reverse), label = "h4"
    )

    Box(
        modifier = modifier
            .width(220.dp)
            .background(Color(0xF209090B), RoundedCornerShape(16.dp))
            .border(1.5.dp, Color(0xFF3F3F46), RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "📻 HIGHWAY FM",
                        color = Color(0xFFF59E0B),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Equalizer visualization
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(12.dp)
                ) {
                    val heights = if (isPlaying) listOf(h1, h2, h3, h4) else listOf(0.2f, 0.2f, 0.2f, 0.2f)
                    heights.forEach { frac ->
                        Box(
                            modifier = Modifier
                                .width(2.5.dp)
                                .fillMaxHeight(frac)
                                .background(if (isPlaying) Color(0xFF10B981) else Color(0xFF52525B), RoundedCornerShape(1.dp))
                        )
                    }
                }
            }

            // Station info display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF030712), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = station.frequency,
                            color = Color(0xFFFACC15),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = station.genre,
                            color = Color(0xFF38BDF8),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = station.name,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = station.tagline,
                        color = Color(0xFFA1A1AA),
                        fontSize = 7.5.sp,
                        maxLines = 1
                    )
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF27272A), RoundedCornerShape(6.dp))
                        .clickable { onPrevStation() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "PREV", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .background(if (isPlaying) Color(0xFFDC2626) else Color(0xFF16A34A), RoundedCornerShape(6.dp))
                        .clickable { onTogglePlay() }
                        .padding(horizontal = 14.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isPlaying) "STOP" else "PLAY",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFF27272A), RoundedCornerShape(6.dp))
                        .clickable { onNextStation() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "NEXT", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
