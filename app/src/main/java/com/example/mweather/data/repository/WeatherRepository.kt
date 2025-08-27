package com.example.mweather.data.repository

import com.example.mweather.BuildConfig
import com.example.mweather.data.api.Resource
import com.example.mweather.data.api.WeatherApiService
import com.example.mweather.data.database.WeatherDao
import com.example.mweather.data.database.WeatherEntity
import com.example.mweather.data.models.CurrentWeatherResponse
import com.example.mweather.data.models.ForecastResponse
import com.example.mweather.domain.repository.WeatherRepositoryInterface
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val gson: Gson
) : WeatherRepositoryInterface {
    
    private val apiKey = BuildConfig.API_KEY.also { 
        android.util.Log.d("WeatherRepository", "API Key initialized: ${if (it.isNotBlank()) "Present" else "Missing"}")
    }
    
    override suspend fun getCurrentWeather(query: String): Flow<Resource<CurrentWeatherResponse>> = flow {
        emit(Resource.Loading())
        
        try {
            val cachedData = getCachedWeather(query)
            if (cachedData != null && !isDataExpired(cachedData.timestamp)) {
                val weatherData = gson.fromJson(cachedData.weatherData, CurrentWeatherResponse::class.java)
                emit(Resource.Success(weatherData))
                return@flow
            }
            
            android.util.Log.d("WeatherRepository", "Fetching current weather for query: $query")
            val response = weatherApiService.getCurrentWeather(apiKey, query)
            
            if (response.isSuccessful && response.body() != null) {
                android.util.Log.d("WeatherRepository", "Current weather fetch successful")
                val weatherData = response.body()!!
                cacheWeatherData(query, weatherData)
                emit(Resource.Success(weatherData))
            } else {
                val errorMsg = "Failed to fetch weather data. Response code: ${response.code()}"
                android.util.Log.e("WeatherRepository", errorMsg)
                
                if (cachedData != null) {
                    android.util.Log.d("WeatherRepository", "Using cached data as fallback")
                    val weatherData = gson.fromJson(cachedData.weatherData, CurrentWeatherResponse::class.java)
                    emit(Resource.Success(weatherData))
                } else {
                    emit(Resource.Error(errorMsg))
                }
            }
        } catch (e: Exception) {
            val cachedData = getCachedWeather(query)
            if (cachedData != null) {
                val weatherData = gson.fromJson(cachedData.weatherData, CurrentWeatherResponse::class.java)
                emit(Resource.Success(weatherData))
            } else {
                emit(Resource.Error("Network error: ${e.message}"))
            }
        }
    }
    
    override suspend fun getForecast(query: String): Flow<Resource<ForecastResponse>> = flow {
        emit(Resource.Loading())
        
        try {
            android.util.Log.d("WeatherRepository", "Fetching forecast for query: $query")
            val response = weatherApiService.getForecast(apiKey, query)
            
            if (response.isSuccessful && response.body() != null) {
                android.util.Log.d("WeatherRepository", "Forecast fetch successful")
                emit(Resource.Success(response.body()!!))
            } else {
                val errorMsg = "Failed to fetch forecast data. Response code: ${response.code()}"
                android.util.Log.e("WeatherRepository", errorMsg)
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            val errorMsg = "Network error while fetching forecast: ${e.message}"
            android.util.Log.e("WeatherRepository", errorMsg, e)
            emit(Resource.Error(errorMsg))
        }
    }
    
    override suspend fun refreshWeatherData(query: String): Flow<Resource<CurrentWeatherResponse>> = flow {
        emit(Resource.Loading())
        
        try {
            val response = weatherApiService.getCurrentWeather(apiKey, query)
            if (response.isSuccessful && response.body() != null) {
                val weatherData = response.body()!!
                cacheWeatherData(query, weatherData)
                emit(Resource.Success(weatherData))
            } else {
                emit(Resource.Error("Failed to refresh weather data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }
    
    private suspend fun getCachedWeather(query: String): WeatherEntity? {
        val locationKey = query.lowercase(Locale.getDefault()).replace(" ", "_")
        return weatherDao.getWeatherByLocation(locationKey)
    }
    
    private suspend fun cacheWeatherData(query: String, weatherData: CurrentWeatherResponse) {
        val locationKey = query.lowercase(Locale.getDefault()).replace(" ", "_")
        val currentTime = System.currentTimeMillis()
        val expirationTime = currentTime + (30 * 60 * 1000) // 30 minutes
        
        val weatherEntity = WeatherEntity(
            locationKey = locationKey,
            weatherData = gson.toJson(weatherData),
            timestamp = currentTime,
            expiresAt = expirationTime
        )
        
        weatherDao.insertWeather(weatherEntity)
    }
    
    private fun isDataExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > (30 * 60 * 1000) // 30 minutes
    }
}