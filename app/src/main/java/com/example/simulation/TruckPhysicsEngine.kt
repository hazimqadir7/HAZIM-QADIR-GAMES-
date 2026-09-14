package com.example.simulation

import com.example.audio.TruckSoundEngine
import com.example.data.models.CameraView
import com.example.data.models.CargoJob
import com.example.data.models.EngineStartPhase
import com.example.data.models.EngineState
import com.example.data.models.FuelStationInfo
import com.example.data.models.GameData
import com.example.data.models.RouteLocation
import com.example.data.models.TelemetryState
import com.example.data.models.TrafficVehicle
import com.example.data.models.TruckModelConfig
import com.example.data.models.WeatherType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

class TruckPhysicsEngine(
    private val soundEngine: TruckSoundEngine,
    var truckModel: TruckModelConfig = GameData.TRUCK_MODELS[0],
    var route: RouteLocation = GameData.ROUTES[0],
    var activeJob: CargoJob? = null,
    var engineUpgradeLevel: Int = 0,
    var brakeUpgradeLevel: Int = 0,
    var suspensionSoftness: Float = 0.85f,
    var wolfExhaust: Boolean = true
) {

    var state = TelemetryState()
        private set

    private val _telemetry = MutableStateFlow(TelemetryState())
    val telemetry: StateFlow<TelemetryState> = _telemetry.asStateFlow()

    private var curThrottle: Float = 0f
    private var curBrake: Float = 0f
    private var curSteer: Float = 0f
    private var curHandbrake: Boolean = false
    private var curCameraView: CameraView = CameraView.THIRD_PERSON
    private var curWeather: WeatherType = WeatherType.DAY_CLEAR
    private var curStrobo: Boolean = true
    private var curWipers: Boolean = false
    private var curRadioPlaying: Boolean = false
    private var curRadioStationIndex: Int = 0

    // Internal physics state
    private var posX: Float = 0f // -4.5f to 4.5f
    private var posZ: Float = 0f
    private var speedKmh: Float = 0f
    private var targetSpeedKmh: Float = 0f
    private var engineRpm: Float = 0f
    private var currentGear: Int = 0
    private var engineState: EngineState = EngineState.OFF
    private var startPhase: EngineStartPhase = EngineStartPhase.OFF
    private var engineTempC: Float = 32f
    private var fuelRemainingPct: Float = 100f
    private var isOutOfFuel: Boolean = false
    private var isRefueling: Boolean = false

    private var steerAngle: Float = 0f
    private var bodyRoll: Float = 0f
    private var bodyRollVelocity: Float = 0f
    private var lastSteerChangeTime: Long = 0L
    private var lastSwingDir: Float = 0f
    private var olengCombo: Int = 0
    private var olengScore: Long = 0L
    private var olengRating: String = "Cruising"

    private var cargoDamagePct: Float = 0f
    private var engineHealthPct: Float = 100f
    private var tireWearPct: Float = 0f
    private var drivingIntensity: Float = 10f

    private var prevThrottle: Float = 0f
    private var turnSignalState: String = "off"
    private var blinkTimer: Float = 0f
    private var blinkState: Boolean = false
    private var ignitionAlertMessage: String? = null
    private var alertTimer: Float = 0f

    // Traffic simulation
    private val traffic = mutableListOf<TrafficVehicle>()

    init {
        resetRoute()
    }

    fun resetRoute() {
        posX = 0f
        posZ = 0f
        speedKmh = 0f
        targetSpeedKmh = 0f
        engineRpm = 0f
        currentGear = 0
        engineState = EngineState.OFF
        startPhase = EngineStartPhase.OFF
        engineTempC = 32f
        fuelRemainingPct = 100f
        isOutOfFuel = false
        isRefueling = false
        cargoDamagePct = 0f
        engineHealthPct = 100f
        tireWearPct = 0f
        olengCombo = 0
        olengScore = 0L
        olengRating = "Cruising"
        bodyRoll = 0f
        bodyRollVelocity = 0f
        spawnTraffic()
        syncState()
    }

    private fun spawnTraffic() {
        traffic.clear()
        val types = listOf("bus", "taxi", "car", "motor", "car", "taxi")
        for (i in 0..5) {
            val lane = if (i % 2 == 0) 2.2f else -2.2f
            traffic.add(
                TrafficVehicle(
                    relX = lane,
                    relZ = -80f - i * 65f,
                    speedKmh = if (types[i] == "bus") 75f else if (types[i] == "motor") 60f else 70f,
                    type = types[i]
                )
            )
        }
    }

    fun startIgnition() {
        if (isOutOfFuel) {
            triggerAlert("DIESEL TANK EMPTY! Refuel before starting engine.")
            return
        }
        if (engineState != EngineState.OFF) return
        engineState = EngineState.STARTING
        startPhase = EngineStartPhase.GLOW
        triggerAlert("GLOW PLUGS PREHEATING... Stand by")

        soundEngine.startEngine(initialRpm = 800f)
        engineState = EngineState.RUNNING
        startPhase = EngineStartPhase.RUNNING
        engineRpm = 800f
        currentGear = 1
        triggerAlert("ENGINE RUNNING! Shift gears & hit the highway.")
    }

    fun stopIgnition() {
        engineState = EngineState.OFF
        startPhase = EngineStartPhase.OFF
        currentGear = 0
        targetSpeedKmh = 0f
        soundEngine.stopEngine()
        triggerAlert("ENGINE SHUT DOWN.")
    }

    fun shiftGear(direction: Int) {
        if (engineState != EngineState.RUNNING) {
            triggerAlert("ENGINE OFF! Press START ENGINE before shifting.")
            return
        }
        val next = (currentGear + direction).coerceIn(-1, 6)
        if (next != currentGear) {
            currentGear = next
            soundEngine.playGearShift()
            if (wolfExhaust) {
                soundEngine.playWolfExhaustWhistle()
            }
        }
    }

    fun triggerHorn() {
        soundEngine.playHorn(truckModel.id)
        // Alert oncoming traffic to pull aside
        traffic.forEach { v ->
            if (abs(v.relZ) < 45f) {
                v.relX += if (v.relX > 0) 1.2f else -1.2f
            }
        }
    }

    fun triggerAlert(message: String) {
        ignitionAlertMessage = message
        alertTimer = 3.5f
    }

    fun refuel() {
        if (isRefueling) return
        isRefueling = true
        fuelRemainingPct = 100f
        isOutOfFuel = false
        soundEngine.playAirBrakes()
        triggerAlert("DIESEL REFUEL COMPLETE! Tank 100%")
        isRefueling = false
    }

    fun toggleTurnSignal(dir: String) {
        turnSignalState = if (turnSignalState == dir) "off" else dir
    }

    fun initTruck(model: TruckModelConfig, softness: Float, job: CargoJob?, newRoute: RouteLocation) {
        truckModel = model
        suspensionSoftness = softness
        activeJob = job
        route = newRoute
        resetRoute()
    }

    fun setThrottle(value: Float) { curThrottle = value.coerceIn(0f, 1f) }
    fun setBrake(value: Float) { curBrake = value.coerceIn(0f, 1f) }
    fun setSteer(value: Float) { curSteer = value.coerceIn(-1f, 1f) }
    fun toggleHandbrake() { curHandbrake = !curHandbrake }
    fun toggleEngine() {
        if (engineState == EngineState.OFF) startIgnition() else stopIgnition()
    }
    fun cycleCamera() {
        curCameraView = when (curCameraView) {
            CameraView.THIRD_PERSON -> CameraView.INTERIOR_COCKPIT
            CameraView.INTERIOR_COCKPIT -> CameraView.BUMPER
            CameraView.BUMPER -> CameraView.THIRD_PERSON
        }
    }
    fun setWeather(w: WeatherType) { curWeather = w }
    fun toggleStrobo() { curStrobo = !curStrobo }
    fun toggleWipers() { curWipers = !curWipers }
    fun shiftUp() { shiftGear(1) }
    fun shiftDown() { shiftGear(-1) }
    fun startRefueling() { refuel() }
    fun toggleRadio() { curRadioPlaying = !curRadioPlaying }
    fun nextRadioStation() { curRadioStationIndex = (curRadioStationIndex + 1) % GameData.RADIO_STATIONS.size }
    fun prevRadioStation() { curRadioStationIndex = (curRadioStationIndex - 1 + GameData.RADIO_STATIONS.size) % GameData.RADIO_STATIONS.size }

    fun update(delta: Float) {
        update(delta, curThrottle, curBrake, curSteer, curHandbrake)
    }

    fun update(
        delta: Float,
        throttleInput: Float,
        brakeInput: Float,
        steerInput: Float,
        handbrake: Boolean
    ) {
        // Alert timer
        if (alertTimer > 0) {
            alertTimer -= delta
            if (alertTimer <= 0) ignitionAlertMessage = null
        }

        // Blink timer for turn signals (approx 2.2 Hz)
        blinkTimer += delta
        if (blinkTimer >= 0.23f) {
            blinkTimer = 0f
            blinkState = !blinkState
        }

        val cargoWeight = activeJob?.weightTons ?: 0f
        val totalGrossWeight = truckModel.weightTons + cargoWeight
        val weightRatio = totalGrossWeight / truckModel.weightTons

        // Gear torque ratios
        val gearTorque = when (currentGear) {
            1 -> 1.75f
            2 -> 1.40f
            3 -> 1.15f
            4 -> 0.95f
            5 -> 0.80f
            6 -> 0.70f
            -1 -> 0.85f
            else -> 0f
        }

        val baseAccel = 35f + (engineUpgradeLevel * 9f)
        val accelPower = (baseAccel * gearTorque) / Math.pow(weightRatio.toDouble(), 0.6).toFloat()
        val baseBrakePower = 65f + (brakeUpgradeLevel * 16f)
        val effectiveBrakePower = baseBrakePower / (1f + (cargoWeight / truckModel.weightTons) * 0.65f)

        // Mountain incline resistance
        val slopeIncline = route.inclineGradePct / 100f
        val gravitySlopeForce = slopeIncline * (totalGrossWeight * 2.5f)

        // Fuel burn calculation
        if (!isOutOfFuel && engineState == EngineState.RUNNING) {
            val idleBurn = 0.02f * delta
            val weightMultiplier = 1f + (cargoWeight / 15f) * 0.45f
            val drivingBurn = (abs(speedKmh) / 70f) * (0.15f + throttleInput * 0.35f) * weightMultiplier * delta
            fuelRemainingPct = max(0f, fuelRemainingPct - (idleBurn + drivingBurn))
            if (fuelRemainingPct <= 0f) {
                fuelRemainingPct = 0f
                isOutOfFuel = true
                stopIgnition()
                triggerAlert("OUT OF FUEL! Truck engine stalled.")
            }
        }

        // Powertrain calculation
        if (engineState == EngineState.RUNNING && !isOutOfFuel) {
            if (currentGear == 0) {
                targetSpeedKmh = 0f
                engineRpm = 800f + throttleInput * 2400f
            } else if (currentGear == -1) {
                targetSpeedKmh = -25f * throttleInput
                engineRpm = 800f + abs(speedKmh) * 45f
            } else {
                val gearMaxSpeeds = floatArrayOf(0f, 28f, 52f, 78f, 105f, 125f, truckModel.topSpeedKmh.toFloat())
                val maxForGear = gearMaxSpeeds[currentGear]
                targetSpeedKmh = maxForGear * throttleInput
                val gearRatio = floatArrayOf(1f, 3.6f, 2.5f, 1.8f, 1.3f, 1.0f, 0.78f)[currentGear]
                val loadRpmBoost = (cargoWeight / 25f) * 180f
                engineRpm = (800f + (abs(speedKmh) * 22f * gearRatio) + loadRpmBoost).coerceIn(800f, 3300f)
            }
        } else {
            targetSpeedKmh = 0f
            engineRpm = 0f
        }

        soundEngine.updateRpm(engineRpm)

        // Knalpot Srigala flutter sound on sudden throttle lift-off at high RPM
        if (wolfExhaust && prevThrottle > 0.6f && throttleInput < 0.15f && engineRpm > 1800f) {
            soundEngine.playWolfExhaustWhistle()
        }
        prevThrottle = throttleInput

        // Accelerate or Decelerate
        if (speedKmh < targetSpeedKmh) {
            val netAccel = max(0.5f, (accelPower / 10f) - (gravitySlopeForce * 0.35f))
            speedKmh = min(targetSpeedKmh, speedKmh + netAccel * delta)
        } else if (speedKmh > targetSpeedKmh) {
            val decel = 4.5f + (if (brakeInput > 0) effectiveBrakePower / 10f else 1.2f)
            speedKmh = max(targetSpeedKmh, speedKmh - decel * delta)
        }

        if (handbrake) {
            speedKmh = max(0f, speedKmh - (90f / weightRatio) * delta)
        }

        // Engine temperature dynamics
        if (engineState == EngineState.RUNNING) {
            val rpmRatio = engineRpm / 3200f
            var targetTemp = 86f + (rpmRatio * 20f)
            if (engineRpm > 2500f) targetTemp += 8f
            if (throttleInput > 0.8f) targetTemp += 5f
            val airflow = (speedKmh / 90f) * 6f
            targetTemp -= airflow
            val rate = if (targetTemp > engineTempC) 0.3f else 0.2f
            engineTempC += (targetTemp - engineTempC) * (rate * delta)
        } else {
            engineTempC = max(32f, engineTempC - 2.5f * delta)
        }

        // Vehicle wear
        var currentIntensity = 10f
        if (speedKmh > 75f) currentIntensity += 25f
        if (engineRpm > 2600f) currentIntensity += 25f
        if (abs(bodyRoll) > 7f) currentIntensity += 25f
        if (brakeInput > 0.7f && speedKmh > 35f) currentIntensity += 20f
        drivingIntensity = (drivingIntensity * 0.95f) + (currentIntensity * 0.05f)

        tireWearPct = min(100f, tireWearPct + delta * 0.008f + (drivingIntensity / 100f) * delta * 0.04f)
        if (engineTempC > 110f) {
            engineHealthPct = max(5f, engineHealthPct - delta * 0.4f)
        }

        // Lateral steering & forward progress
        val speedMs = (speedKmh * 1000f) / 3600f
        posZ -= speedMs * delta
        val steerSensitivity = 3.8f / (1f + (cargoWeight / 25f) * 0.4f)
        posX += steerInput * steerSensitivity * (speedKmh / 50f) * delta
        posX = posX.coerceIn(-4.6f, 4.6f)
        steerAngle = (steerAngle * 0.85f) + (steerInput * 0.45f * 0.15f)

        // "Truk Oleng" / Pahadi Sway suspension roll
        val cargoCoMMultiplier = 1f + (cargoWeight / truckModel.weightTons) * 0.5f
        val swayMultiplier = suspensionSoftness * 18f * cargoCoMMultiplier
        val speedFactor = (speedKmh / 60f).coerceIn(0f, 1.4f)
        val targetRoll = -steerInput * swayMultiplier * speedFactor
        val springStiffness = 18f / Math.pow(cargoCoMMultiplier.toDouble(), 0.35).toFloat()
        val damping = 4.2f / Math.pow(cargoCoMMultiplier.toDouble(), 0.25).toFloat()
        val rollForce = (targetRoll - bodyRoll) * springStiffness
        bodyRollVelocity += rollForce * delta
        bodyRollVelocity -= bodyRollVelocity * damping * delta
        bodyRoll += bodyRollVelocity * delta
        bodyRoll = bodyRoll.coerceIn(-22f, 22f)

        // Sway combo scoring (alternating sways while driving above 40 km/h)
        val currentSwingDir = sign(steerInput)
        if (abs(steerInput) > 0.6f && currentSwingDir != 0f && currentSwingDir != lastSwingDir) {
            val now = System.currentTimeMillis()
            if (now - lastSteerChangeTime < 750 && speedKmh > 40f) {
                olengCombo = min(10, olengCombo + 1)
                olengScore += 150L * olengCombo
                olengRating = when {
                    olengCombo >= 6 -> "ULTRA HIGHWAY SWAY!"
                    olengCombo >= 4 -> "INSANE TRUCK DRIFT!"
                    else -> "CLEAN SWAY COMBO!"
                }
                soundEngine.playTireScreech()
            }
            lastSwingDir = currentSwingDir
            lastSteerChangeTime = now
        }
        if (System.currentTimeMillis() - lastSteerChangeTime > 2200) {
            olengCombo = 0
            olengRating = "Cruising"
        }

        // Heavy cargo tilt damage
        if (abs(bodyRoll) > 17f && cargoWeight > 10f) {
            cargoDamagePct = min(100f, cargoDamagePct + 0.4f * delta)
        }

        // Traffic AI updates
        traffic.forEach { v ->
            val vSpeedMs = (v.speedKmh * 1000f) / 3600f
            v.relZ += (speedMs - vSpeedMs) * delta

            // Recycle traffic
            if (v.relZ > 45f) {
                v.relZ = -220f - (Math.random().toFloat() * 60f)
                v.relX = if (Math.random() > 0.5) 2.2f else -2.2f
            } else if (v.relZ < -300f) {
                v.relZ = 30f
            }

            // Collision check
            if (abs(v.relZ) < 3.5f && abs(v.relX - posX) < 1.4f) {
                speedKmh *= 0.6f
                cargoDamagePct = min(100f, cargoDamagePct + 8f)
                soundEngine.playCollision()
                v.relX += if (v.relX > posX) 1.5f else -1.5f
            }
        }

        // Check nearest fuel station
        val currentDist = -posZ
        val stations = GameData.ROUTE_FUEL_STATIONS[route.id] ?: emptyList()
        var minDist = 9999f
        var nearest: FuelStationInfo? = null
        stations.forEach { s ->
            val d = abs(currentDist - s.distanceMeters)
            if (d < minDist) {
                minDist = d
                nearest = s
            }
        }

        val isNearSPBU = minDist <= 35f
        val isFinished = activeJob != null && currentDist >= 3000f

        syncState(
            throttle = throttleInput,
            brake = brakeInput,
            steer = steerInput,
            handbrake = handbrake,
            isNearSPBU = isNearSPBU,
            nearestSPBU = nearest,
            distSPBU = minDist,
            finished = isFinished
        )
    }

    private fun syncState(
        throttle: Float = 0f,
        brake: Float = 0f,
        steer: Float = 0f,
        handbrake: Boolean = false,
        isNearSPBU: Boolean = false,
        nearestSPBU: FuelStationInfo? = null,
        distSPBU: Float = 9999f,
        finished: Boolean = false
    ) {
        state = TelemetryState(
            speedKmh = speedKmh,
            engineRpm = engineRpm,
            currentGear = currentGear,
            engineState = engineState,
            startPhase = startPhase,
            engineTempC = engineTempC,
            fuelRemainingPct = fuelRemainingPct,
            isOutOfFuel = isOutOfFuel,
            isRefueling = isRefueling,
            isNearFuelStation = isNearSPBU,
            nearestFuelStation = nearestSPBU,
            distToNearestFuelStation = distSPBU,
            throttleInput = throttle,
            brakeInput = brake,
            steerInput = steer,
            isHandbrakeActive = handbrake,
            isJakeBrakeActive = false,
            bodyRollAngle = bodyRoll,
            olengCombo = olengCombo,
            olengScore = olengScore,
            olengRatingText = olengRating,
            distanceTraveledMeters = max(0f, -posZ),
            totalRouteMeters = 3000f,
            cargoDamagePct = cargoDamagePct,
            engineHealthPct = engineHealthPct,
            tireWearPct = tireWearPct,
            tireHealthPct = max(0f, 100f - tireWearPct),
            drivingIntensity = drivingIntensity,
            drivingIntensityLevel = if (drivingIntensity > 75f) "extreme" else if (drivingIntensity > 50f) "aggressive" else if (drivingIntensity > 25f) "moderate" else "safe",
            cameraView = curCameraView,
            weather = curWeather,
            stroboActive = curStrobo,
            wipersActive = curWipers,
            turnSignal = turnSignalState,
            turnSignalBlink = turnSignalState != "off" && blinkState,
            radioPlaying = curRadioPlaying,
            radioStationIndex = curRadioStationIndex,
            ignitionAlertMessage = ignitionAlertMessage,
            trafficList = traffic,
            isJobFinished = finished
        )
        _telemetry.value = state
    }
}
