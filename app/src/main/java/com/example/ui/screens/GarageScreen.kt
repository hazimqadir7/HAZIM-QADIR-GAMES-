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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TruckSoundEngine
import com.example.data.database.PlayerProfileEntity
import com.example.data.models.DashboardLightColor
import com.example.data.models.GameData
import com.example.ui.components.AnalogFuelGauge
import com.example.ui.components.AnalogSpeedometer
import com.example.ui.components.AnalogTachometer

@Composable
fun GarageScreen(
    profile: PlayerProfileEntity,
    soundEngine: TruckSoundEngine,
    onSelectTruck: (String) -> Unit,
    onBuyTruck: (String, Long) -> Unit,
    onSelectLivery: (String) -> Unit,
    onBuyLivery: (String, Long) -> Unit,
    onSelectHorn: (String) -> Unit,
    onBuyHorn: (String, Long) -> Unit,
    onSelectDashboardColor: (String) -> Unit,
    onApplyUpgrade: (String, Long) -> Unit,
    onOpenJobSelect: () -> Unit,
    onStartFreeRoam: () -> Unit,
    onOpenAchievements: () -> Unit
) {
    var activeTab by remember { mutableStateOf("trucks") } // "trucks", "liveries", "horns", "upgrades", "lights"
    val ownedTrucks = remember(profile.unlockedTrucksCsv) { profile.unlockedTrucksCsv.split(",").toSet() }
    val ownedLiveries = remember(profile.unlockedLiveriesCsv) { profile.unlockedLiveriesCsv.split(",").toSet() }
    val ownedHorns = remember(profile.unlockedHornsCsv) { profile.unlockedHornsCsv.split(",").toSet() }

    val currentTruck = GameData.TRUCK_MODELS.find { it.id == profile.selectedTruckId } ?: GameData.TRUCK_MODELS[0]
    val currentLivery = GameData.LIVERY_OPTIONS.find { it.id == profile.selectedLiveryId } ?: GameData.LIVERY_OPTIONS[0]
    val currentLightColor = DashboardLightColor.values().find { it.idName == profile.dashboardLightColor } ?: DashboardLightColor.AMBER

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Header: Title, Level Rank, and INR Wallet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "TRUCK SIMULATOR KASHMIR",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFDC2626), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "HIMALAYAN GARAGE",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    val title = GameData.DRIVER_TITLES.getOrElse(profile.driverLevel - 1) { "Himalayan Legend" }
                    Text(
                        text = "Level ${profile.driverLevel}: $title • ${profile.driverXp} XP",
                        color = Color(0xFFFDE047),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Achievements button
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF27272A), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(12.dp))
                            .clickable { onOpenAchievements() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🏆 ACHIEVEMENTS",
                            color = Color(0xFFF59E0B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // INR Wallet
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF18181B), RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "₹", color = Color(0xFF10B981), fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Text(
                                text = "${profile.moneyInr}",
                                color = Color(0xFF10B981),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Navigation Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "trucks" to "🚛 Trucks",
                    "liveries" to "🎨 Truck Art Liveries",
                    "horns" to "🎺 Musical Horns",
                    "upgrades" to "⚡ Performance Tuning",
                    "lights" to "💡 Dashboard Lights"
                ).forEach { (id, label) ->
                    val isSel = activeTab == id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSel) Color(0xFFF59E0B) else Color(0xFF18181B),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { activeTab = id }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSel) Color.Black else Color(0xFFA1A1AA),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF18181B), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                when (activeTab) {
                    "trucks" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(GameData.TRUCK_MODELS) { truck ->
                                val isOwned = ownedTrucks.contains(truck.id)
                                val isEquipped = profile.selectedTruckId == truck.id

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isEquipped) Color(0xFF1E1B4B) else Color(0xFF09090B), RoundedCornerShape(12.dp))
                                        .border(1.dp, if (isEquipped) Color(0xFFF59E0B) else Color(0xFF27272A), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Box(modifier = Modifier.background(Color(0xFF27272A), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                                    Text(truck.badge, color = Color(0xFFFACC15), fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                                }
                                                Text(truck.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                                if (isEquipped) {
                                                    Text("EQUIPPED ✓", color = Color(0xFF10B981), fontSize = 8.5.sp, fontWeight = FontWeight.Black)
                                                }
                                            }

                                            Text(truck.description, color = Color(0xFFA1A1AA), fontSize = 9.sp)

                                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 4.dp)) {
                                                Text("Power: ${truck.enginePowerHp} HP", color = Color(0xFFE2E8F0), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                                Text("Top Speed: ${truck.topSpeedKmh} KM/H", color = Color(0xFFF59E0B), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                                Text("Weight: ${truck.weightTons} T", color = Color(0xFF38BDF8), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                                Text("Sway: ${(truck.olengResponsiveness * 100).toInt()}%", color = Color(0xFF10B981), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                            }
                                        }

                                        if (isOwned) {
                                            Button(
                                                onClick = { onSelectTruck(truck.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = if (isEquipped) Color(0xFF10B981) else Color(0xFFF59E0B)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text(if (isEquipped) "ACTIVE" else "SELECT", color = if (isEquipped) Color.White else Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                            }
                                        } else {
                                            Button(
                                                onClick = { onBuyTruck(truck.id, truck.price) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("BUY ₹${truck.price}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "liveries" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(GameData.LIVERY_OPTIONS) { livery ->
                                val isOwned = ownedLiveries.contains(livery.id)
                                val isEquipped = profile.selectedLiveryId == livery.id
                                val isLevelLocked = livery.requiredLevel > profile.driverLevel

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isEquipped) Color(0xFF1E1B4B) else Color(0xFF09090B), RoundedCornerShape(12.dp))
                                        .border(1.dp, if (isEquipped) Color(0xFFF59E0B) else Color(0xFF27272A), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Palette swatches
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Box(modifier = Modifier.size(14.dp).background(Color(livery.primaryColorHex), CircleShape))
                                                Box(modifier = Modifier.size(14.dp).background(Color(livery.secondaryColorHex), CircleShape))
                                                Box(modifier = Modifier.size(14.dp).background(Color(livery.tarpaulinColorHex), CircleShape))
                                            }

                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text(livery.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                                    Text("• ${livery.subtitle}", color = Color(0xFFA1A1AA), fontSize = 9.sp)
                                                }
                                                Text("\"${livery.quoteText}\"", color = Color(0xFFFACC15), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                Text("Mudflap: ${livery.mudflapText} | Banner: ${livery.windshieldBanner}", color = Color(0xFF94A3B8), fontSize = 8.5.sp)
                                            }
                                        }

                                        if (isLevelLocked) {
                                            Text("🔒 Requires Level ${livery.requiredLevel}", color = Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        } else if (isOwned) {
                                            Button(
                                                onClick = { onSelectLivery(livery.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = if (isEquipped) Color(0xFF10B981) else Color(0xFFF59E0B)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text(if (isEquipped) "ACTIVE" else "SELECT", color = if (isEquipped) Color.White else Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                            }
                                        } else {
                                            Button(
                                                onClick = { onBuyLivery(livery.id, livery.price) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("BUY ₹${livery.price}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "horns" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(GameData.HORN_OPTIONS) { horn ->
                                val isOwned = ownedHorns.contains(horn.id)
                                val isEquipped = profile.selectedHornId == horn.id
                                val isLevelLocked = horn.requiredLevel > profile.driverLevel

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isEquipped) Color(0xFF1E1B4B) else Color(0xFF09090B), RoundedCornerShape(12.dp))
                                        .border(1.dp, if (isEquipped) Color(0xFFF59E0B) else Color(0xFF27272A), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(horn.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                                Box(modifier = Modifier.background(Color(0xFF27272A), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                                    Text(horn.category, color = Color(0xFF38BDF8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF27272A), RoundedCornerShape(8.dp))
                                                    .clickable { soundEngine.playHorn(horn.id) }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                            ) {
                                                Text("TEST 🔊", color = Color(0xFFFACC15), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                            }

                                            if (isLevelLocked) {
                                                Text("🔒 Level ${horn.requiredLevel}", color = Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            } else if (isOwned) {
                                                Button(
                                                    onClick = { onSelectHorn(horn.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = if (isEquipped) Color(0xFF10B981) else Color(0xFFF59E0B)),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(if (isEquipped) "ACTIVE" else "EQUIP", color = if (isEquipped) Color.White else Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                                }
                                            } else {
                                                Button(
                                                    onClick = { onBuyHorn(horn.id, horn.price) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("BUY ₹${horn.price}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "upgrades" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Engine Bore-up
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF09090B), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Turbo Diesel Bore-Up (Level ${profile.engineUpgradeLevel}/3)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Boosts hill-climbing acceleration torque on steep mountain passes", color = Color(0xFFA1A1AA), fontSize = 9.sp)
                                    }
                                    Button(
                                        onClick = { onApplyUpgrade("engine", 25000L) },
                                        enabled = profile.engineUpgradeLevel < 3,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (profile.engineUpgradeLevel >= 3) "MAXED" else "UPGRADE ₹25,000", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }

                            // High-pressure Air Brakes
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF09090B), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("High-Pressure Air Brakes (Level ${profile.brakeUpgradeLevel}/3)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Cuts stopping distance when loaded with heavy 20+ ton timber and cement", color = Color(0xFFA1A1AA), fontSize = 9.sp)
                                    }
                                    Button(
                                        onClick = { onApplyUpgrade("brake", 25000L) },
                                        enabled = profile.brakeUpgradeLevel < 3,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (profile.brakeUpgradeLevel >= 3) "MAXED" else "UPGRADE ₹25,000", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }

                            // Leaf-spring Softness
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF09090B), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Leaf-Spring Suspension Tuning (${(profile.suspensionSoftness * 100).toInt()}%)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Enables dramatic centrifugal body roll & higher drift scores on hairpins", color = Color(0xFFA1A1AA), fontSize = 9.sp)
                                    }
                                    Button(
                                        onClick = { onApplyUpgrade("suspension", 25000L) },
                                        enabled = profile.suspensionSoftness < 1.0f,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (profile.suspensionSoftness >= 1.0f) "MAXED" else "TUNE ₹25,000", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }

                    "lights" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Live Cluster Preview
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnalogTachometer(rpm = 2100f, dashboardColor = currentLightColor)
                                Spacer(modifier = Modifier.width(14.dp))
                                AnalogSpeedometer(speedKmh = 68f, currentGear = 4, dashboardColor = currentLightColor)
                                Spacer(modifier = Modifier.width(14.dp))
                                AnalogFuelGauge(fuelPct = 85f, dashboardColor = currentLightColor)
                            }

                            // Palette Choices
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(DashboardLightColor.values()) { colorOpt ->
                                    val isSelected = colorOpt.idName == profile.dashboardLightColor
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (isSelected) Color(0xFF1E1B4B) else Color(0xFF09090B), RoundedCornerShape(10.dp))
                                            .border(1.dp, if (isSelected) Color(colorOpt.primaryColorHex) else Color(0xFF27272A), RoundedCornerShape(10.dp))
                                            .clickable { onSelectDashboardColor(colorOpt.idName) }
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Box(modifier = Modifier.size(16.dp).background(Color(colorOpt.primaryColorHex), CircleShape))
                                                Column {
                                                    Text(colorOpt.displayName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text(colorOpt.description, color = Color(0xFFA1A1AA), fontSize = 8.5.sp)
                                                }
                                            }
                                            if (isSelected) {
                                                Text("EQUIPPED ✓", color = Color(colorOpt.primaryColorHex), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Truck: ${currentTruck.name} • Livery: ${currentLivery.name}",
                    color = Color(0xFFA1A1AA),
                    fontSize = 10.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onStartFreeRoam,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("FREE ROAM CRUISE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }

                    Button(
                        onClick = onOpenJobSelect,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SELECT EXPEDITION CONTRACT ➔", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
