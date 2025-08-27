package com.example.mweather.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [WeatherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    
    abstract fun weatherDao(): WeatherDao
    
    companion object {
        const val DATABASE_NAME = "weather_database"
    }
}