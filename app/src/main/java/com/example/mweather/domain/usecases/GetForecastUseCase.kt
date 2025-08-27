package com.example.mweather.domain.usecases

import com.example.mweather.data.api.Resource
import com.example.mweather.data.models.ForecastResponse
import com.example.mweather.domain.repository.WeatherRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepositoryInterface
) {
    suspend operator fun invoke(query: String): Flow<Resource<ForecastResponse>> {
        return repository.getForecast(query)
    }
}