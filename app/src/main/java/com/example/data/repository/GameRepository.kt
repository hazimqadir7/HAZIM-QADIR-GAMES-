package com.example.data.repository

import com.example.data.database.PlayerProfileDao
import com.example.data.database.PlayerProfileEntity
import com.example.data.models.GameData
import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: PlayerProfileDao) {

    val profileFlow: Flow<PlayerProfileEntity?> = dao.getProfileFlow()

    suspend fun getOrCreateProfile(): PlayerProfileEntity {
        var profile = dao.getProfile()
        if (profile == null) {
            profile = PlayerProfileEntity()
            dao.insertProfile(profile)
        }
        return profile
    }

    suspend fun saveProfile(profile: PlayerProfileEntity) {
        dao.insertProfile(profile)
    }

    suspend fun selectTruck(truckId: String) {
        dao.updateSelectedTruck(truckId)
    }

    suspend fun selectLivery(liveryId: String) {
        dao.updateSelectedLivery(liveryId)
    }

    suspend fun selectHorn(hornId: String) {
        dao.updateSelectedHorn(hornId)
    }

    suspend fun selectDashboardColor(color: String) {
        dao.updateDashboardLightColor(color)
    }

    suspend fun buyTruck(truckId: String, price: Long): Boolean {
        val current = getOrCreateProfile()
        if (current.moneyInr < price) return false
        val owned = current.unlockedTrucksCsv.split(",").toMutableSet()
        owned.add(truckId)
        val updated = current.copy(
            moneyInr = current.moneyInr - price,
            unlockedTrucksCsv = owned.joinToString(","),
            selectedTruckId = truckId
        )
        dao.insertProfile(updated)
        return true
    }

    suspend fun buyLivery(liveryId: String, price: Long): Boolean {
        val current = getOrCreateProfile()
        if (current.moneyInr < price) return false
        val owned = current.unlockedLiveriesCsv.split(",").toMutableSet()
        owned.add(liveryId)
        val updated = current.copy(
            moneyInr = current.moneyInr - price,
            unlockedLiveriesCsv = owned.joinToString(","),
            selectedLiveryId = liveryId
        )
        dao.insertProfile(updated)
        return true
    }

    suspend fun buyHorn(hornId: String, price: Long): Boolean {
        val current = getOrCreateProfile()
        if (current.moneyInr < price) return false
        val owned = current.unlockedHornsCsv.split(",").toMutableSet()
        owned.add(hornId)
        val updated = current.copy(
            moneyInr = current.moneyInr - price,
            unlockedHornsCsv = owned.joinToString(","),
            selectedHornId = hornId
        )
        dao.insertProfile(updated)
        return true
    }

    suspend fun applyUpgrade(type: String, cost: Long): Boolean {
        val current = getOrCreateProfile()
        if (current.moneyInr < cost) return false
        val updated = when (type) {
            "engine" -> current.copy(
                moneyInr = current.moneyInr - cost,
                engineUpgradeLevel = minOf(3, current.engineUpgradeLevel + 1)
            )
            "brake" -> current.copy(
                moneyInr = current.moneyInr - cost,
                brakeUpgradeLevel = minOf(3, current.brakeUpgradeLevel + 1)
            )
            "suspension" -> current.copy(
                moneyInr = current.moneyInr - cost,
                suspensionSoftness = minOf(1.0f, current.suspensionSoftness + 0.15f)
            )
            else -> current
        }
        dao.insertProfile(updated)
        return true
    }

    suspend fun claimAchievement(badgeId: String, rewardMoney: Long, rewardXp: Long): Boolean {
        val current = getOrCreateProfile()
        val claimed = current.claimedAchievementsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
        if (claimed.contains(badgeId)) return false
        claimed.add(badgeId)
        val newXp = current.driverXp + rewardXp
        val newLevel = calculateLevel(newXp)
        val updated = current.copy(
            moneyInr = current.moneyInr + rewardMoney,
            driverXp = newXp,
            driverLevel = newLevel,
            claimedAchievementsCsv = claimed.joinToString(",")
        )
        dao.insertProfile(updated)
        return true
    }

    suspend fun recordDeliveryComplete(
        earnedMoney: Long,
        earnedXp: Long,
        distanceTraveledMeters: Long,
        peakOlengScore: Long,
        isPristine: Boolean
    ) {
        val current = getOrCreateProfile()
        val nextXp = current.driverXp + earnedXp
        val nextLevel = calculateLevel(nextXp)
        val updated = current.copy(
            moneyInr = current.moneyInr + earnedMoney,
            driverXp = nextXp,
            driverLevel = nextLevel,
            completedJobsCount = current.completedJobsCount + 1,
            totalDistanceMeters = current.totalDistanceMeters + distanceTraveledMeters,
            highOlengScore = maxOf(current.highOlengScore, peakOlengScore),
            cleanDeliveriesCount = current.cleanDeliveriesCount + (if (isPristine) 1 else 0)
        )
        dao.insertProfile(updated)
    }

    suspend fun recordRefuel(fuelCostInr: Long) {
        val current = getOrCreateProfile()
        val updated = current.copy(
            moneyInr = maxOf(0L, current.moneyInr - fuelCostInr),
            refuelCount = current.refuelCount + 1
        )
        dao.insertProfile(updated)
    }

    private fun calculateLevel(xp: Long): Int {
        val thresholds = listOf(0L, 300L, 750L, 1400L, 2300L, 3500L, 5000L, 7000L, 9500L, 12500L)
        for (i in thresholds.indices.reversed()) {
            if (xp >= thresholds[i]) return i + 1
        }
        return 1
    }
}
