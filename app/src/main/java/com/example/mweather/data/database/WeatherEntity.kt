package com.example.mweather.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey val locationKey: String,
    val weatherData: String, 
    val timestamp: Long,
    val expiresAt: Long
)