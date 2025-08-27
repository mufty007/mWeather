package com.example.mweather.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mweather.data.api.Resource
import com.example.mweather.data.models.CurrentWeatherResponse
import com.example.mweather.domain.usecases.GetCurrentWeatherUseCase
import com.example.mweather.domain.usecases.GetLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    private val _weatherState = MutableStateFlow<Resource<CurrentWeatherResponse>>(Resource.Loading())
    val weatherState: StateFlow<Resource<CurrentWeatherResponse>> = _weatherState.asStateFlow()

    private var currentQuery: String? = null

    fun getCurrentLocationWeather() {
        viewModelScope.launch {
            try {
                val location = getLocationUseCase.getCurrentLocation() 
                    ?: getLocationUseCase.getLastKnownLocation()
                
                if (location != null) {
                    val query = "${location.latitude},${location.longitude}"
                    currentQuery = query
                    fetchWeather(query)
                } else {
                    _weatherState.value = Resource.Error("Unable to get current location")
                }
            } catch (e: Exception) {
                _weatherState.value = Resource.Error("Location error: ${e.message}")
            }
        }
    }

    fun refreshWeatherData() {
        currentQuery?.let { query ->
            viewModelScope.launch {
                getCurrentWeatherUseCase.refresh(query)
                    .collect { resource ->
                        _weatherState.value = resource
                    }
            }
        } ?: getCurrentLocationWeather()
    }

    fun getWeatherByCoordinates(lat: Double, lon: Double) {
        val query = "${lat},${lon}"
        currentQuery = query
        fetchWeather(query)
    }

    fun getWeatherByCity(cityName: String) {
        if (cityName.isBlank()) {
            _weatherState.value = Resource.Error("Please enter a valid city name")
            return
        }
        
        currentQuery = cityName
        fetchWeather(cityName)
    }

    private fun fetchWeather(query: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("MainViewModel", "Fetching weather for: $query")
                _weatherState.value = Resource.Loading()
                
                getCurrentWeatherUseCase(query).collect { resource ->
                    android.util.Log.d("MainViewModel", "Weather resource received: ${resource::class.simpleName}")
                    _weatherState.value = resource
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error fetching weather for $query", e)
                _weatherState.value = Resource.Error("Failed to fetch weather: ${e.message}")
            }
        }
    }
}