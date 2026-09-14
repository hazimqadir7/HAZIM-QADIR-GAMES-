package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CargoJob
import com.example.data.models.GameData
import com.example.data.models.RouteLocation
import com.example.data.models.WeatherType

@Composable
fun JobSelectScreen(
    onStartJob: (CargoJob, RouteLocation, WeatherType) -> Unit,
    onBack: () -> Unit
) {
    var selectedJob by remember { mutableStateOf(GameData.CARGO_JOBS[0]) }
    var selectedWeather by remember { mutableStateOf(WeatherType.DAY_CLEAR) }
    val activeRoute = GameData.ROUTES.find { it.id == selectedJob.routeId } ?: GameData.ROUTES[0]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF27272A), RoundedCornerShape(8.dp))
                            .clickable { onBack() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "← GARAGE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }

                    Column {
                        Text(
                            text = "HIMALAYAN EXPEDITION & LOGISTICS CONTRACTS",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Select cargo consignment, mountain corridor, and departure atmosphere",
                            color = Color(0xFFA1A1AA),
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Main Two-Column Layout
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Column: Cargo Contract Cards
                LazyColumn(
                    modifier = Modifier.weight(1.1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(GameData.CARGO_JOBS) { job ->
                        val isSelected = selectedJob.id == job.id
                        val routeInfo = GameData.ROUTES.find { it.id == job.routeId }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) Color(0xFF1E1B4B) else Color(0xFF18181B),
                                    RoundedCornerShape(14.dp)
                                )
                                .border(
                                    1.5.dp,
                                    if (isSelected) Color(0xFFF59E0B) else Color(0xFF27272A),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedJob = job }
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF27272A), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                    ) {
                                        Text(text = job.category.uppercase(), color = Color(0xFFFDE047), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text(
                                        text = "₹${job.baseRewardInr}",
                                        color = Color(0xFF10B981),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(text = job.cargoName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "${job.originCity} ➔ ${job.destinationCity}",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${job.weightTons} TONS ${if (job.weightTons > 18) "(HEAVY)" else ""}",
                                        color = if (job.weightTons > 18) Color(0xFFEF4444) else Color(0xFFFACC15),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (routeInfo != null) {
                                        Text(
                                            text = routeInfo.name.split(" ")[0],
                                            color = Color(0xFFA1A1AA),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Right Column: Route Details, Weight Physics, Weather & Depart Button
                Column(
                    modifier = Modifier.weight(0.9f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Route Topology
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF18181B), RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "🏔️ ROUTE TOPOLOGY", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Text(text = activeRoute.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = activeRoute.description, color = Color(0xFFA1A1AA), fontSize = 8.5.sp, lineHeight = 11.sp)

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Grade: +${activeRoute.inclineGradePct}%", color = Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Difficulty: ${activeRoute.difficulty}", color = Color(0xFFFACC15), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Region: ${activeRoute.region}", color = Color(0xFF38BDF8), fontSize = 9.sp)
                            }
                        }
                    }

                    // Cargo Weight Dynamics Advisory
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF18181B), RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "⚖️ CARGO WEIGHT PHYSICS", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Text(text = "${selectedJob.weightTons} TONS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                            }

                            val brakePenalty = (selectedJob.weightTons * 5.5f).toInt()
                            Text(
                                text = "Braking Distance: +$brakePenalty%  •  Climb Torque: ${if (selectedJob.weightTons > 18) "Downshift G1/G2 Required" else "Smooth Pull"}",
                                color = if (selectedJob.weightTons > 18) Color(0xFFEF4444) else Color(0xFF10B981),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Weather Selector
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF18181B), RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "⛅ DEPARTURE WEATHER", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Black)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                WeatherType.values().forEach { w ->
                                    val isSel = selectedWeather == w
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isSel) Color(0xFFF59E0B) else Color(0xFF27272A),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedWeather = w }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (w) {
                                                WeatherType.DAY_CLEAR -> "Day ☀️"
                                                WeatherType.SUNSET -> "Sunset 🌅"
                                                WeatherType.NIGHT -> "Night 🌙"
                                                WeatherType.RAIN_STORM -> "Monsoon 🌧️"
                                            },
                                            color = if (isSel) Color.Black else Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Depart Button
                    Button(
                        onClick = { onStartJob(selectedJob, activeRoute, selectedWeather) },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "DEPART & HIT THE GAS! ➔",
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
}
