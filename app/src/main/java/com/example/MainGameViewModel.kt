package com.example

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.TruckSoundEngine
import com.example.data.database.PlayerProfileEntity
import com.example.data.database.TruckSimDatabase
import com.example.data.models.AchievementBadge
import com.example.data.models.CargoJob
import com.example.data.models.DashboardLightColor
import com.example.data.models.GameData
import com.example.data.models.LiveryOption
import com.example.data.models.RouteLocation
import com.example.data.models.TelemetryState
import com.example.data.models.TruckModelConfig
import com.example.data.models.WeatherType
import com.example.data.repository.GameRepository
import com.example.simulation.TruckPhysicsEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainGameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val soundEngine: TruckSoundEngine = TruckSoundEngine(application)
    val physicsEngine: TruckPhysicsEngine = TruckPhysicsEngine(soundEngine)

    private val vibrator: Vibrator? = application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    private val _screenState = MutableStateFlow("GARAGE") // "GARAGE", "JOB_SELECT", "DRIVING"
    val screenState: StateFlow<String> = _screenState.asStateFlow()

    private val _profileState = MutableStateFlow<PlayerProfileEntity?>(null)
    val profileState: StateFlow<PlayerProfileEntity?> = _profileState.asStateFlow()

    val telemetryState: StateFlow<TelemetryState> = physicsEngine.telemetry

    private val _activeJob = MutableStateFlow<CargoJob?>(null)
    val activeJob: StateFlow<CargoJob?> = _activeJob.asStateFlow()

    private val _activeRoute = MutableStateFlow(GameData.ROUTES[0])
    val activeRoute: StateFlow<RouteLocation> = _activeRoute.asStateFlow()

    private val _showPause = MutableStateFlow(false)
    val showPause: StateFlow<Boolean> = _showPause.asStateFlow()

    private val _showDeliveryComplete = MutableStateFlow(false)
    val showDeliveryComplete: StateFlow<Boolean> = _showDeliveryComplete.asStateFlow()

    private val _showAchievements = MutableStateFlow(false)
    val showAchievements: StateFlow<Boolean> = _showAchievements.asStateFlow()

    private var simulationLoopJob: Job? = null

    init {
        val db = TruckSimDatabase.getInstance(application)
        repository = GameRepository(db.playerProfileDao())

        viewModelScope.launch {
            // Ensure profile exists in DB
            repository.getOrCreateProfile()

            repository.profileFlow.collect { entity ->
                _profileState.value = entity
                if (entity != null) {
                    val truck = GameData.TRUCK_MODELS.find { it.id == entity.selectedTruckId } ?: GameData.TRUCK_MODELS[0]
                    physicsEngine.initTruck(truck, entity.suspensionSoftness, _activeJob.value, _activeRoute.value)
                }
            }
        }
    }

    fun getActiveTruck(): TruckModelConfig {
        val currentProfile = _profileState.value
        val truckId = currentProfile?.selectedTruckId ?: "tata_1613"
        return GameData.TRUCK_MODELS.find { it.id == truckId } ?: GameData.TRUCK_MODELS[0]
    }

    fun getActiveLivery(): LiveryOption {
        val currentProfile = _profileState.value
        val liveryId = currentProfile?.selectedLiveryId ?: "chinar_express"
        return GameData.LIVERY_OPTIONS.find { it.id == liveryId } ?: GameData.LIVERY_OPTIONS[0]
    }

    fun getActiveDashboardColor(): DashboardLightColor {
        val currentProfile = _profileState.value
        val colorName = currentProfile?.dashboardLightColor ?: "amber"
        return DashboardLightColor.values().find { it.idName == colorName } ?: DashboardLightColor.AMBER
    }

    fun openJobSelect() {
        _screenState.value = "JOB_SELECT"
    }

    fun returnToGarage() {
        stopSimulation()
        _showPause.value = false
        _showDeliveryComplete.value = false
        _screenState.value = "GARAGE"
    }

    fun startJob(job: CargoJob, route: RouteLocation, weather: WeatherType) {
        _activeJob.value = job
        _activeRoute.value = route
        val profile = _profileState.value
        val truck = getActiveTruck()
        physicsEngine.initTruck(truck, profile?.suspensionSoftness ?: 0.85f, job, route)
        physicsEngine.setWeather(weather)
        _showDeliveryComplete.value = false
        _showPause.value = false
        _screenState.value = "DRIVING"
        startSimulation()
    }

    fun startFreeRoam() {
        _activeJob.value = null
        _activeRoute.value = GameData.ROUTES[0]
        val profile = _profileState.value
        val truck = getActiveTruck()
        physicsEngine.initTruck(truck, profile?.suspensionSoftness ?: 0.85f, null, GameData.ROUTES[0])
        _showDeliveryComplete.value = false
        _showPause.value = false
        _screenState.value = "DRIVING"
        startSimulation()
    }

    private fun startSimulation() {
        simulationLoopJob?.cancel()
        simulationLoopJob = viewModelScope.launch {
            val stepSeconds = 0.016f // 60 FPS
            while (isActive) {
                if (!_showPause.value && !_showDeliveryComplete.value) {
                    physicsEngine.update(stepSeconds)

                    val telem = physicsEngine.telemetry.value
                    // Check for completion
                    if (_activeJob.value != null && telem.distanceTraveledMeters >= telem.totalRouteMeters && !_showDeliveryComplete.value) {
                        _showDeliveryComplete.value = true
                        triggerHaptic(120)
                    }
                }
                delay(16)
            }
        }
    }

    private fun stopSimulation() {
        simulationLoopJob?.cancel()
        simulationLoopJob = null
        soundEngine.stopEngine()
    }

    fun pauseGame() {
        _showPause.value = true
    }

    fun resumeGame() {
        _showPause.value = false
    }

    fun restartMission() {
        val job = _activeJob.value
        val route = _activeRoute.value
        val weather = physicsEngine.telemetry.value.weather
        if (job != null) {
            startJob(job, route, weather)
        } else {
            startFreeRoam()
        }
    }

    fun playEquippedHorn() {
        val hornId = _profileState.value?.selectedHornId ?: "basuri_v3"
        soundEngine.playHorn(hornId)
        triggerHaptic(80)
    }

    fun triggerHaptic(millis: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(millis)
            }
        } catch (_: Exception) {}
    }

    fun claimDeliveryReward(earnedMoney: Long, earnedXp: Long) {
        viewModelScope.launch {
            val telem = physicsEngine.telemetry.value
            repository.recordDeliveryComplete(
                earnedMoney = earnedMoney,
                earnedXp = earnedXp,
                distanceTraveledMeters = telem.distanceTraveledMeters.toLong(),
                peakOlengScore = telem.olengScore,
                isPristine = telem.cargoDamagePct < 5f
            )
            returnToGarage()
        }
    }

    fun selectTruck(truckId: String) {
        viewModelScope.launch { repository.selectTruck(truckId) }
    }

    fun buyTruck(truckId: String, price: Long) {
        viewModelScope.launch {
            val success = repository.buyTruck(truckId, price)
            if (success) triggerHaptic(80)
        }
    }

    fun selectLivery(liveryId: String) {
        viewModelScope.launch { repository.selectLivery(liveryId) }
    }

    fun buyLivery(liveryId: String, price: Long) {
        viewModelScope.launch {
            val success = repository.buyLivery(liveryId, price)
            if (success) triggerHaptic(80)
        }
    }

    fun selectHorn(hornId: String) {
        viewModelScope.launch { repository.selectHorn(hornId) }
    }

    fun buyHorn(hornId: String, price: Long) {
        viewModelScope.launch {
            val success = repository.buyHorn(hornId, price)
            if (success) triggerHaptic(80)
        }
    }

    fun selectDashboardColor(colorName: String) {
        viewModelScope.launch { repository.selectDashboardColor(colorName) }
    }

    fun applyUpgrade(upgradeType: String, cost: Long) {
        viewModelScope.launch {
            val success = repository.applyUpgrade(upgradeType, cost)
            if (success) triggerHaptic(80)
        }
    }

    fun openAchievements() {
        _showAchievements.value = true
    }

    fun closeAchievements() {
        _showAchievements.value = false
    }

    fun claimAchievement(badge: AchievementBadge) {
        viewModelScope.launch {
            val success = repository.claimAchievement(badge.id, badge.rewardMoneyInr, badge.rewardXp)
            if (success) triggerHaptic(100)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSimulation()
        soundEngine.release()
    }
}
