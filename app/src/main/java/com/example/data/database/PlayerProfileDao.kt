package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getProfileFlow(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getProfile(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: PlayerProfileEntity)

    @Update
    suspend fun updateProfile(profile: PlayerProfileEntity)

    @Query("UPDATE player_profile SET moneyInr = :newBalance WHERE id = 1")
    suspend fun updateMoney(newBalance: Long)

    @Query("UPDATE player_profile SET selectedTruckId = :truckId WHERE id = 1")
    suspend fun updateSelectedTruck(truckId: String)

    @Query("UPDATE player_profile SET selectedLiveryId = :liveryId WHERE id = 1")
    suspend fun updateSelectedLivery(liveryId: String)

    @Query("UPDATE player_profile SET selectedHornId = :hornId WHERE id = 1")
    suspend fun updateSelectedHorn(hornId: String)

    @Query("UPDATE player_profile SET dashboardLightColor = :color WHERE id = 1")
    suspend fun updateDashboardLightColor(color: String)
}
