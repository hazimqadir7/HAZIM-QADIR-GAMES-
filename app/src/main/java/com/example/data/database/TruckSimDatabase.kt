package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlayerProfileEntity::class], version = 1, exportSchema = false)
abstract class TruckSimDatabase : RoomDatabase() {
    abstract fun playerProfileDao(): PlayerProfileDao

    companion object {
        @Volatile
        private var INSTANCE: TruckSimDatabase? = null

        fun getInstance(context: Context): TruckSimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TruckSimDatabase::class.java,
                    "truck_simulator_kashmir.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
