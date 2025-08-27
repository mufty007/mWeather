package com.example.mweather.domain.usecases

import android.location.Location
import com.example.mweather.presentation.utils.LocationManager
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationManager: LocationManager
) {
    suspend fun getCurrentLocation(): Location? {
        return locationManager.getCurrentLocation()
    }
    
    suspend fun getLastKnownLocation(): Location? {
        return locationManager.getLastKnownLocation()
    }
    
    fun hasLocationPermission(): Boolean {
        return locationManager.hasLocationPermission()
    }
}