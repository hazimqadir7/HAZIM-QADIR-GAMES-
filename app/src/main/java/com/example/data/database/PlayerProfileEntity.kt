package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val driverName: String = "Sultan of Kashmir",
    val moneyInr: Long = 250000L, // Starting balance: ₹2,50,000 INR
    val driverLevel: Int = 1,
    val driverXp: Long = 0L,
    val reputation: Int = 1,
    val selectedTruckId: String = "tata_1613",
    val selectedLiveryId: String = "chinar_express",
    val selectedHornId: String = "basuri_v3",
    val tarpaulinType: String = "SEGITIGA",
    val dashboardLightColor: String = "amber",
    val stroboEnabled: Boolean = true,
    val underglowEnabled: Boolean = true,
    val underglowColor: String = "#3B82F6",
    val engineUpgradeLevel: Int = 0,
    val brakeUpgradeLevel: Int = 0,
    val suspensionSoftness: Float = 0.85f,
    val unlockedTrucksCsv: String = "tata_1613",
    val unlockedLiveriesCsv: String = "chinar_express,pari_mahal",
    val unlockedHornsCsv: String = "basuri_v3,dual_air",
    val completedJobsCount: Int = 0,
    val totalDistanceMeters: Long = 0L,
    val highOlengScore: Long = 0L,
    val cleanDeliveriesCount: Int = 0,
    val refuelCount: Int = 0,
    val claimedAchievementsCsv: String = ""
)
