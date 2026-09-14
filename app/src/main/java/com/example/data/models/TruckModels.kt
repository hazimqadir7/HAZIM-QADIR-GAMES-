package com.example.data.models

enum class WeatherType(val displayName: String) {
    DAY_CLEAR("Day / Clear"),
    SUNSET("Sunset / Golden"),
    NIGHT("Night / Starry"),
    RAIN_STORM("Monsoon Rain")
}

enum class CameraView(val displayName: String) {
    THIRD_PERSON("Chase View"),
    INTERIOR_COCKPIT("Cabin Cockpit"),
    BUMPER("Bumper View")
}

enum class EngineState {
    OFF,
    STARTING,
    RUNNING
}

enum class EngineStartPhase {
    OFF,
    GLOW,
    CRANKING,
    CAUGHT,
    RUNNING
}

enum class TarpaulinType(val displayName: String, val description: String) {
    SEGITIGA("Triangular Tarpaulin", "Aerodynamic peaked tarp popular with express fresh fruit haulers"),
    KOTAK("Standard Box Tarpaulin", "Durable weather cover for general valley freight and packages"),
    GAYOR_TINGGI("High Overload Gayor", "Towering lashed stack for massive agricultural produce"),
    TERBUKA("Open Hardwood Bed", "Traditional carved wooden bed without cover for bulk timber and steel")
}

enum class DashboardLightColor(
    val idName: String,
    val displayName: String,
    val subName: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val description: String
) {
    AMBER("amber", "Warm Saffron Amber", "Classic Kashmiri Halogen", 0xFFF59E0BL, 0xFFD97706L, "Authentic halogen amber backlighting signature to Tata 1613 and Ashok Leyland trucks on NH-44."),
    BLUE("blue", "Dal Lake Ice Blue", "Modern Mountain Express", 0xFF0EA5E9L, 0xFF0284C7L, "Crisp ice blue illumination favored by modern Himalayan long-haul express logistics rigs."),
    WHITE("white", "Snow White Pure", "Gulmarg Snow Luminescence", 0xFFF8FAFCL, 0xFFCBD5E1L, "High-contrast luminescent white dials offering maximum clarity during foggy mountain passes."),
    EMERALD("emerald", "Pine Valley Emerald", "Himalayan Diesel Green", 0xFF10B981L, 0xFF059669L, "Soothing commercial diesel green phosphor dials for overnight endurance mountain hauls."),
    CRIMSON("crimson", "Kashmiri Apple Crimson", "Sport Mountain Drift", 0xFFEF4444L, 0xFFB91C1CL, "Fierce midnight red dials engineered for aggressive throttle response and mountain pass sprints."),
    PURPLE("purple", "Zafran Violet", "Custom Truck Art", 0xFFA855F7L, 0xFF7E22CEL, "Vibrant saffron petal purple illumination inspired by decorated Kashmiri bridal trucks.")
}

data class TruckModelConfig(
    val id: String,
    val name: String,
    val badge: String,
    val price: Long,
    val enginePowerHp: Int,
    val topSpeedKmh: Int,
    val weightTons: Float,
    val olengResponsiveness: Float,
    val description: String
)

data class LiveryOption(
    val id: String,
    val name: String,
    val subtitle: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val accentColorHex: Long,
    val tarpaulinColorHex: Long,
    val quoteText: String,
    val mudflapText: String,
    val sideText: String,
    val windshieldBanner: String,
    val price: Long,
    val requiredLevel: Int = 1
)

data class HornOption(
    val id: String,
    val name: String,
    val category: String,
    val price: Long,
    val requiredLevel: Int = 1,
    val melodyNotes: List<HornNote>
)

data class HornNote(
    val frequencyHz: Float,
    val durationMs: Int
)

data class RouteLocation(
    val id: String,
    val name: String,
    val region: String,
    val lengthKm: Float,
    val difficulty: String,
    val inclineGradePct: Int,
    val curvesDensity: Int,
    val description: String,
    val altitudeMeters: Int = 2850
)

data class CargoJob(
    val id: String,
    val cargoName: String,
    val category: String,
    val weightTons: Float,
    val originCity: String,
    val destinationCity: String,
    val routeId: String,
    val baseRewardInr: Long,
    val fragility: Float,
    val timeLimitSec: Int
)

data class FuelStationInfo(
    val id: String,
    val name: String,
    val distanceMeters: Float,
    val pricePerLiterInr: Float,
    val hasRestArea: Boolean,
    val side: String // "left" or "right"
)

data class RouteTurnPoint(
    val distanceMeters: Float,
    val name: String,
    val direction: String, // "hairpin_left", "hairpin_right", "sharp_left", "sharp_right", "gentle_left", "gentle_right", "s_curve"
    val angleDegrees: Float,
    val advisorySpeedKmh: Int,
    val inclineGradePct: Int,
    val tacticalTip: String
)

data class RadioStation(
    val id: String,
    val name: String,
    val frequency: String,
    val genre: String,
    val tagline: String,
    val currentProgram: String,
    val bpm: Int
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val kashmirTitle: String,
    val description: String,
    val category: String, // "distance", "jobs", "oleng", "garage", "endurance"
    val rarity: String, // "bronze", "silver", "gold", "diamond", "legendary"
    val targetValue: Long,
    val unit: String,
    val rewardMoneyInr: Long,
    val rewardXp: Long
)

data class TrafficVehicle(
    var relX: Float,
    var relZ: Float,
    var speedKmh: Float,
    val type: String // "bus", "taxi", "car", "motor"
)

data class TelemetryState(
    val speedKmh: Float = 0f,
    val engineRpm: Float = 0f,
    val currentGear: Int = 0, // -1 = R, 0 = N, 1..6
    val engineState: EngineState = EngineState.OFF,
    val startPhase: EngineStartPhase = EngineStartPhase.OFF,
    val engineTempC: Float = 32f,
    val fuelRemainingPct: Float = 100f,
    val isOutOfFuel: Boolean = false,
    val isRefueling: Boolean = false,
    val isNearFuelStation: Boolean = false,
    val nearestFuelStation: FuelStationInfo? = null,
    val distToNearestFuelStation: Float = 9999f,
    val throttleInput: Float = 0f,
    val brakeInput: Float = 0f,
    val steerInput: Float = 0f,
    val isHandbrakeActive: Boolean = false,
    val isJakeBrakeActive: Boolean = false,
    val bodyRollAngle: Float = 0f, // degrees of mountain sway
    val olengCombo: Int = 0,
    val olengScore: Long = 0,
    val olengRatingText: String = "Cruising",
    val distanceTraveledMeters: Float = 0f,
    val totalRouteMeters: Float = 3500f,
    val cargoDamagePct: Float = 0f,
    val engineHealthPct: Float = 100f,
    val tireWearPct: Float = 0f,
    val tireHealthPct: Float = 100f,
    val drivingIntensity: Float = 10f,
    val drivingIntensityLevel: String = "safe",
    val headlights: String = "low", // "off", "low", "high"
    val stroboActive: Boolean = true,
    val wipersActive: Boolean = false,
    val turnSignal: String = "off", // "off", "left", "right", "hazard"
    val turnSignalBlink: Boolean = false,
    val ignitionAlertMessage: String? = null,
    val cameraView: CameraView = CameraView.THIRD_PERSON,
    val weather: WeatherType = WeatherType.DAY_CLEAR,
    val radioPlaying: Boolean = false,
    val radioStationIndex: Int = 0,
    val trafficList: List<TrafficVehicle> = emptyList(),
    val isJobFinished: Boolean = false
)
