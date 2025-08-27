package com.example.mweather.domain.usecases

import com.example.mweather.data.api.Resource
import com.example.mweather.data.models.CurrentWeatherResponse
import com.example.mweather.domain.repository.WeatherRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepositoryInterface
) {
    suspend operator fun invoke(query: String): Flow<Resource<CurrentWeatherResponse>> {
        return repository.getCurrentWeather(query)
    }
    
    suspend fun refresh(query: String): Flow<Resource<CurrentWeatherResponse>> {
        return repository.refreshWeatherData(query)
    }
}