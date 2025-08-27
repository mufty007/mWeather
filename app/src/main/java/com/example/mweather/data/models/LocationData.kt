package com.example.mweather.data.models

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val countryCode: String
)

// For search suggestions, we'll use the Location model from WeatherAPI responses
// No separate geocoding service needed