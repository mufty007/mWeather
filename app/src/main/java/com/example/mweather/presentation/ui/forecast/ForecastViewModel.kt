package com.example.mweather.presentation.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mweather.data.api.Resource
import com.example.mweather.data.models.ForecastResponse
import com.example.mweather.domain.usecases.GetForecastUseCase
import com.example.mweather.domain.usecases.GetLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getForecastUseCase: GetForecastUseCase,
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    private val _forecastState = MutableStateFlow<Resource<ForecastResponse>>(Resource.Loading())
    val forecastState: StateFlow<Resource<ForecastResponse>> = _forecastState.asStateFlow()

    private var currentQuery: String? = null

    init {
        getCurrentLocationForecast()
    }

    fun getCurrentLocationForecast() {
        viewModelScope.launch {
            try {
                val location = getLocationUseCase.getCurrentLocation() 
                    ?: getLocationUseCase.getLastKnownLocation()
                
                if (location != null) {
                    val query = "${location.latitude},${location.longitude}"
                    currentQuery = query
                    getForecast(query)
                } else {
                    _forecastState.value = Resource.Error("Unable to get current location")
                }
            } catch (e: Exception) {
                _forecastState.value = Resource.Error("Location error: ${e.message}")
            }
        }
    }

    fun getForecastByCoordinates(lat: Double, lon: Double) {
        val query = "${lat},${lon}"
        currentQuery = query
        getForecast(query)
    }

    fun getForecastByCity(cityName: String) {
        if (cityName.isBlank()) {
            _forecastState.value = Resource.Error("Please enter a valid city name")
            return
        }
        
        currentQuery = cityName
        getForecast(cityName)
    }

    fun refreshForecastData() {
        currentQuery?.let { query ->
            getForecast(query)
        } ?: getCurrentLocationForecast()
    }

    private fun getForecast(query: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("ForecastViewModel", "Fetching forecast for: $query")
                _forecastState.value = Resource.Loading()
                
                getForecastUseCase(query).collect { resource ->
                    android.util.Log.d("ForecastViewModel", "Forecast resource received: ${resource::class.simpleName}")
                    _forecastState.value = resource
                }
            } catch (e: Exception) {
                android.util.Log.e("ForecastViewModel", "Error fetching forecast for $query", e)
                _forecastState.value = Resource.Error("Failed to fetch forecast: ${e.message}")
            }
        }
    }
}