package com.example.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CameraView
import com.example.data.models.CargoJob
import com.example.data.models.DashboardLightColor
import com.example.data.models.EngineState
import com.example.data.models.GameData
import com.example.data.models.LiveryOption
import com.example.data.models.RouteLocation
import com.example.data.models.TelemetryState
import com.example.data.models.TruckModelConfig
import com.example.data.models.WeatherType
import com.example.simulation.TruckPhysicsEngine
import com.example.ui.components.AnalogFuelGauge
import com.example.ui.components.AnalogSpeedometer
import com.example.ui.components.AnalogTachometer
import com.example.ui.components.MiniMapRadar
import com.example.ui.components.OlengMeter
import com.example.ui.components.RadioPlayerView
import com.example.ui.components.SteeringWheelView
import com.example.ui.components.VehicleConditionCard

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrivingHudScreen(
    telemetry: TelemetryState,
    physicsEngine: TruckPhysicsEngine,
    truckModel: TruckModelConfig,
    livery: LiveryOption,
    route: RouteLocation,
    activeJob: CargoJob?,
    dashboardColor: DashboardLightColor,
    onPlayHorn: () -> Unit,
    onPauseClicked: () -> Unit
) {
    var isRadioExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Full-screen 3D Driving Renderer
        TruckCanvasRenderer(
            telemetry = telemetry,
            truckModel = truckModel,
            livery = livery,
            route = route,
            activeJob = activeJob,
            modifier = Modifier.fillMaxSize()
        )

        // 2. HUD Overlay Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR: Navigation, Oleng Meter, Radar & Quick Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Top Left: Route Header, Camera, Weather & Pause
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Pause Button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xD918181B), CircleShape)
                                .border(1.dp, Color(0xFF52525B), CircleShape)
                                .clickable { onPauseClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("❚❚", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }

                        // Route Altitude Pill
                        Box(
                            modifier = Modifier
                                .background(Color(0xD909090B), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Column {
                                Text(
                                    text = route.name.uppercase(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "ALTITUDE: ${route.altitudeMeters}m • GRADE: +${route.inclineGradePct}%",
                                    color = Color(0xFFFDE047),
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Camera View Toggle
                        Box(
                            modifier = Modifier
                                .background(Color(0xD918181B), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(10.dp))
                                .clickable { physicsEngine.cycleCamera() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "📷 ${telemetry.cameraView.name.replace("_", " ")}",
                                color = Color.White,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Weather Toggle
                        Box(
                            modifier = Modifier
                                .background(Color(0xD918181B), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(10.dp))
                                .clickable {
                                    val next = when (telemetry.weather) {
                                        WeatherType.DAY_CLEAR -> WeatherType.SUNSET
                                        WeatherType.SUNSET -> WeatherType.NIGHT
                                        WeatherType.NIGHT -> WeatherType.RAIN_STORM
                                        WeatherType.RAIN_STORM -> WeatherType.DAY_CLEAR
                                    }
                                    physicsEngine.setWeather(next)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = when (telemetry.weather) {
                                    WeatherType.DAY_CLEAR -> "☀️ DAY"
                                    WeatherType.SUNSET -> "🌅 SUNSET"
                                    WeatherType.NIGHT -> "🌙 NIGHT"
                                    WeatherType.RAIN_STORM -> "🌧️ RAIN"
                                },
                                color = Color.White,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Radio Expand Toggle
                        Box(
                            modifier = Modifier
                                .background(Color(0xD918181B), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(10.dp))
                                .clickable { isRadioExpanded = !isRadioExpanded }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "📻 RADIO",
                                color = if (isRadioExpanded) Color(0xFFF59E0B) else Color.White,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Radio Panel Floating
                    if (isRadioExpanded) {
                        val currentStation = GameData.RADIO_STATIONS.getOrElse(telemetry.radioStationIndex) { GameData.RADIO_STATIONS[0] }
                        RadioPlayerView(
                            station = currentStation,
                            isPlaying = telemetry.radioPlaying,
                            onTogglePlay = { physicsEngine.toggleRadio() },
                            onNextStation = { physicsEngine.nextRadioStation() },
                            onPrevStation = { physicsEngine.prevRadioStation() }
                        )
                    }
                }

                // Top Center: Mountain Sway Oleng Meter
                OlengMeter(
                    bodyRollAngle = telemetry.bodyRollAngle,
                    olengCombo = telemetry.olengCombo,
                    olengScore = telemetry.olengScore,
                    olengRatingText = telemetry.olengRatingText
                )

                // Top Right: Refueling & MiniMap Radar
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (telemetry.isNearFuelStation) {
                        Box(
                            modifier = Modifier
                                .background(if (telemetry.isRefueling) Color(0xFF16A34A) else Color(0xFFEA580C), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                                .clickable { physicsEngine.startRefueling() }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (telemetry.isRefueling) "⛽ PUMPING DIESEL..." else "⛽ INDIAN OIL (REFUEL ₹5,000)",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    MiniMapRadar(
                        telemetry = telemetry,
                        route = route,
                        activeJob = activeJob
                    )
                }
            }

            // BOTTOM BAR: Steering Wheel, Gauges Cluster, Vehicle Health & Pedals
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Bottom Left: Steering Wheel & Auxiliary Controls
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick Push-to-Steer Arrows (Touch alternative)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Turn Left Arrow
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xD918181B), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(8.dp))
                                    .pointerInteropFilter { event ->
                                        when (event.action) {
                                            MotionEvent.ACTION_DOWN -> { physicsEngine.setSteer(-0.75f); true }
                                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { physicsEngine.setSteer(0f); true }
                                            else -> false
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("◀", color = Color.White, fontSize = 14.sp)
                            }

                            // Turn Right Arrow
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xD918181B), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(8.dp))
                                    .pointerInteropFilter { event ->
                                        when (event.action) {
                                            MotionEvent.ACTION_DOWN -> { physicsEngine.setSteer(0.75f); true }
                                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { physicsEngine.setSteer(0f); true }
                                            else -> false
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("▶", color = Color.White, fontSize = 14.sp)
                            }
                        }

                        // Strobe & Wipers Row
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Strobe Lightbar Toggle
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(if (telemetry.stroboActive) Color(0xFF3B82F6) else Color(0xD918181B), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(8.dp))
                                    .clickable { physicsEngine.toggleStrobo() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🚨", fontSize = 12.sp)
                            }

                            // Windshield Wipers Toggle
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(if (telemetry.wipersActive) Color(0xFF10B981) else Color(0xD918181B), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(8.dp))
                                    .clickable { physicsEngine.toggleWipers() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🌧️", fontSize = 12.sp)
                            }
                        }
                    }

                    // Rotating Steering Wheel with Center Horn Button
                    SteeringWheelView(
                        onSteer = { physicsEngine.setSteer(it) },
                        onHorn = { onPlayHorn() },
                        sizeDp = 118.dp
                    )
                }

                // Bottom Center: Realistic Analog Gauge Cluster & Transmission Controls
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Engine Start/Stop & Gear Column
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Engine Ignition Switch
                        val isRunning = telemetry.engineState == EngineState.RUNNING
                        Box(
                            modifier = Modifier
                                .background(if (isRunning) Color(0xFF16A34A) else Color(0xFFDC2626), RoundedCornerShape(8.dp))
                                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                                .clickable { physicsEngine.toggleEngine() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isRunning) "ENGINE ON" else "START ENG",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Gear selector buttons [R] [N] [D]
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            listOf("R" to -1, "N" to 0, "D" to 1).forEach { (lbl, g) ->
                                val isCur = if (g == 1) telemetry.currentGear > 0 else telemetry.currentGear == g
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(if (isCur) Color(0xFFF59E0B) else Color(0xFF27272A), RoundedCornerShape(4.dp))
                                        .clickable {
                                            if (g == -1) physicsEngine.shiftGear(-1)
                                            else if (g == 0) physicsEngine.shiftGear(0)
                                            else if (g == 1 && telemetry.currentGear <= 0) physicsEngine.shiftGear(1)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(lbl, color = if (isCur) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        // Manual Downshift / Upshift taps [G-] [G+]
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFF27272A), RoundedCornerShape(4.dp))
                                    .clickable { physicsEngine.shiftDown() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("-", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFF27272A), RoundedCornerShape(4.dp))
                                    .clickable { physicsEngine.shiftUp() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        // Active Gear indicator
                        Text(
                            text = if (telemetry.currentGear == -1) "GEAR: R" else if (telemetry.currentGear == 0) "GEAR: N" else "GEAR: G${telemetry.currentGear}",
                            color = Color(0xFFFACC15),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Analog Tachometer (RPM)
                    AnalogTachometer(
                        rpm = telemetry.engineRpm,
                        dashboardColor = dashboardColor
                    )

                    // Analog Speedometer (KM/H)
                    AnalogSpeedometer(
                        speedKmh = telemetry.speedKmh,
                        currentGear = telemetry.currentGear,
                        tripKm = telemetry.distanceTraveledMeters / 1000f,
                        dashboardColor = dashboardColor
                    )

                    // Analog Fuel Gauge
                    AnalogFuelGauge(
                        fuelPct = telemetry.fuelRemainingPct,
                        isRefueling = telemetry.isRefueling,
                        dashboardColor = dashboardColor
                    )
                }

                // Bottom Right: Vehicle Condition Card & Drive Pedals
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Vehicle Wear & Handbrake
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        // Handbrake button
                        Box(
                            modifier = Modifier
                                .background(if (telemetry.isHandbrakeActive) Color(0xFFDC2626) else Color(0xFF27272A), RoundedCornerShape(8.dp))
                                .border(1.dp, if (telemetry.isHandbrakeActive) Color.White else Color(0xFF3F3F46), RoundedCornerShape(8.dp))
                                .clickable { physicsEngine.toggleHandbrake() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PARKING (P)",
                                color = Color.White,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        VehicleConditionCard(
                            engineHealthPct = telemetry.engineHealthPct,
                            tireWearPct = telemetry.tireWearPct,
                            cargoDamagePct = telemetry.cargoDamagePct,
                            engineTempC = telemetry.engineTempC,
                            modifier = Modifier.width(135.dp)
                        )
                    }

                    // Foot Pedals: BRAKE and GAS Pedals (Touch-and-Hold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // BRAKE Pedal
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(95.dp)
                                .background(
                                    if (telemetry.brakeInput > 0.1f) Color(0xFFDC2626) else Color(0xFF1E293B),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(2.dp, Color(0xFF475569), RoundedCornerShape(8.dp))
                                .pointerInteropFilter { event ->
                                    when (event.action) {
                                        MotionEvent.ACTION_DOWN -> { physicsEngine.setBrake(1f); true }
                                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { physicsEngine.setBrake(0f); true }
                                        else -> false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("BRAKE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                Text("AIR", color = Color(0xFFA1A1AA), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        // GAS Pedal (Taller)
                        Box(
                            modifier = Modifier
                                .width(52.dp)
                                .height(115.dp)
                                .background(
                                    if (telemetry.throttleInput > 0.1f) Color(0xFF16A34A) else Color(0xFF1E293B),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(2.dp, Color(0xFF475569), RoundedCornerShape(8.dp))
                                .pointerInteropFilter { event ->
                                    when (event.action) {
                                        MotionEvent.ACTION_DOWN -> { physicsEngine.setThrottle(1f); true }
                                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { physicsEngine.setThrottle(0f); true }
                                        else -> false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("GAS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                Text("DRIVE", color = Color(0xFFA1A1AA), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}
