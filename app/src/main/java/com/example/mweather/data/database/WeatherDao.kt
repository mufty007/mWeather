package com.example.mweather.data.database

import androidx.room.*

@Dao
interface WeatherDao {
    
    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey LIMIT 1")
    suspend fun getWeatherByLocation(locationKey: String): WeatherEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)
    
    @Delete
    suspend fun deleteWeather(weather: WeatherEntity)
    
    @Query("DELETE FROM weather_cache WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredData(currentTime: Long)
    
    @Query("DELETE FROM weather_cache")
    suspend fun clearAllCache()
    
    @Query("SELECT * FROM weather_cache")
    suspend fun getAllCachedWeather(): List<WeatherEntity>
}