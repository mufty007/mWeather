package com.example.mweather.domain.repository

import com.example.mweather.data.api.Resource
import com.example.mweather.data.models.CurrentWeatherResponse
import com.example.mweather.data.models.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepositoryInterface {
    
    suspend fun getCurrentWeather(query: String): Flow<Resource<CurrentWeatherResponse>>
    
    suspend fun getForecast(query: String): Flow<Resource<ForecastResponse>>
    
    suspend fun refreshWeatherData(query: String): Flow<Resource<CurrentWeatherResponse>>
}